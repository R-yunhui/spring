package com.ral.young.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.ral.young.bo.MetricsQueryRange;
import com.ral.young.bo.NodeResourceVariationInfo;
import com.ral.young.bo.ResourceAlarmMessage;
import com.ral.young.bo.ResourceAlarmRule;
import com.ral.young.handler.WsMessageBroadcaster;
import com.ral.young.service.ResourceAlarmMessageService;
import com.ral.young.service.ResourceAlarmRuleService;
import com.ral.young.service.ResourceMonitorService;
import com.ral.young.service.impl.ResourceMonitorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author renyunhui
 * @description 这是一个ResourceAlarmTask类
 * @date 2024-09-20 15-39-59
 * @since 1.0.0
 */
@Component
@Slf4j
public class ResourceAlarmTask implements ApplicationRunner {

    @Resource
    private ResourceAlarmMessageService resourceAlarmMessageService;

    @Resource
    private ResourceAlarmRuleService resourceAlarmRuleService;

    @Resource
    private ResourceMonitorService resourceMonitorService;

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    @Resource
    private RedisDistributedLock<String> redisDistributedLock;

    @Resource
    private FeignLicenseClientService feignLicenseClientService;

    @DubboReference
    private TenantApiService tenantApiService;

    private static final String START_TIME = "start::time";

    private static final String REDIS_LOCK_KEY = "resource::alarm::lock::key";

    private static final String REDIS_PLAT_FORM_LOCK_KEY = "platform::alarm::lock::key";

    private static final String REDIS_TENANT_LOCK_KEY = "tenant::alarm::lock::key";

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1L,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(20), ThreadFactoryBuilder.create().setNamePrefix("resource-alarm-").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        executor.prestartAllCoreThreads();
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void dealResourceAlarmTask() {
        String value = Thread.currentThread().getId() + "-" + "01";
        try {
            if (redisDistributedLock.lock(REDIS_LOCK_KEY, value, Duration.ofSeconds(60))) {
                log.info("===== 开始统计资源信息，判断是否超过了配置的阈值 =====");
                long start = System.currentTimeMillis();
                List<ResourceAlarmRule> resourceAlarmRules = resourceAlarmRuleService.queryAllResourceAlarmRule();
                List<ResourceAlarmMessage> resourceAlarmMessageList = new ArrayList<>();
                List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
                // 按照同一个资源同一个步长进行分组，减少调用 Prometheus 接口的次数
                Map<String, List<ResourceAlarmRule>> resourceAlarmRuleGroupMap = resourceAlarmRules.stream().collect(Collectors.groupingBy(resourceAlarmRule -> resourceAlarmRule.getResourceEnum().name() + resourceAlarmRule.getTimeDuration()));
                resourceAlarmRuleGroupMap.forEach((k, v) -> {
                    // 同一个资源，同一不长只需要查询一次接口
                    // 通过 Prometheus 查询数据，判断对应的资源告警是否满足条件
                    if (CollUtil.isNotEmpty(v)) {
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> doAlarmCheck(v, resourceAlarmMessageList), executor);
                        completableFutures.add(future);
                    }
                });

                CompletableFuture<Void> future = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
                try {
                    // 等待所有任务执行完毕之后进行入库操作
                    future.get();

                    //  批量入库告警数据
                    if (CollUtil.isNotEmpty(resourceAlarmMessageList)) {
                        resourceAlarmMessageService.generatedAlarmList(resourceAlarmMessageList);
                        log.info("===== 统计资源信息完成，耗时：{}ms =====", (System.currentTimeMillis() - start));
                    }
                } catch (Exception e) {
                    log.error("处理告警数据异常，", e);
                }
            }
        } finally {
            // 释放锁
            String lockValue = redisDistributedLock.getLockValue(REDIS_LOCK_KEY);
            if (value.equals(lockValue)) {
                redisDistributedLock.unlock(REDIS_LOCK_KEY);
            }
        }
    }

    // @Scheduled(cron = "0 1 * * * ?")
    @Scheduled(cron = "0 */10 * * * ?")
    public void dealPlaFormAuthAlarmTask() {
        String value = Thread.currentThread().getId() + "-" + "02";
        try {
            log.info("===== 开始检测平台授权过期 =====");
            if (redisDistributedLock.lock(REDIS_PLAT_FORM_LOCK_KEY, value, Duration.ofSeconds(10))) {
                LicenseVO detail = feignLicenseClientService.detail();
                LocalDateTime expireTime = detail.getExpireTime();
                // 剩余天数小于1天，则告警
                if (DateUtil.between(DateUtil.date(expireTime), DateUtil.date(), DateUnit.DAY) <= 1) {
                    resourceAlarmMessageService.createAuthResourceAlarmMessage(ResourceEnum.PLATFORM_AUTH, ResourceAlarmConstants.DEFAULT_TENANT_ID, ResourceAlarmConstants.ALARM_MESSAGE);
                }
                log.info("===== 检测平台授权过期完成 =====");
            }
        } catch (Exception e) {
            log.error("检测平台授权过期异常，e", e);
        } finally {
            // 释放锁
            String lockValue = redisDistributedLock.getLockValue(REDIS_PLAT_FORM_LOCK_KEY);
            if (value.equals(lockValue)) {
                redisDistributedLock.unlock(REDIS_PLAT_FORM_LOCK_KEY);
            }
        }
    }

    // @Scheduled(cron = "0 1 * * * ?")
    @Scheduled(cron = "0 */10 * * * ?")
    public void dealTenantAuthAlarmTask() {
        String value = Thread.currentThread().getId() + "-" + "03";
        try {
            log.info("===== 开始检测租户授权过期 =====");
            if (redisDistributedLock.lock(REDIS_TENANT_LOCK_KEY, value, Duration.ofSeconds(10))) {
                List<Long> allTenantIds = tenantApiService.getAllTenantIds();
                if (CollUtil.isEmpty(allTenantIds)) {
                    log.error("查询不到租户信息");
                    return;
                }

                Set<Long> tenantIdSet = new HashSet<>(allTenantIds);
                List<TenantLicenseVO> tenantLicenseVOList = feignLicenseClientService.getLicenseByTenantIds(new IdsDTO(tenantIdSet));
                if (CollUtil.isNotEmpty(tenantLicenseVOList)) {
                    Map<Long, TenantLicenseVO> tenantLicenseVOMap = tenantLicenseVOList.stream().collect(Collectors.toMap(TenantLicenseVO::getTenantId, o -> o));
                    for (Long tenantId : tenantIdSet) {
                        TenantLicenseVO tenantLicenseVO = tenantLicenseVOMap.get(tenantId);
                        LicenseVO licenseVO = tenantLicenseVO.getLicenseVO();
                        if (null != licenseVO) {
                            LocalDateTime expireTime = licenseVO.getExpireTime();
                            // 剩余天数小于1天，则告警
                            if (DateUtil.between(DateUtil.date(expireTime), DateUtil.date(), DateUnit.DAY) <= 1) {
                                resourceAlarmMessageService.createAuthResourceAlarmMessage(ResourceEnum.TENANT_AUTH, ResourceAlarmConstants.DEFAULT_TENANT_ID, ResourceAlarmConstants.ALARM_MESSAGE);
                            }
                        }
                    }
                }
                log.info("===== 检测租户授权过期完成 =====");
            }
        } catch (Exception e) {
            log.error("检测租户授权过期异常，e", e);
        } finally {
            // 释放锁
            String lockValue = redisDistributedLock.getLockValue(REDIS_TENANT_LOCK_KEY);
            if (value.equals(lockValue)) {
                redisDistributedLock.unlock(REDIS_TENANT_LOCK_KEY);
            }
        }
    }

    private void doAlarmCheck(List<ResourceAlarmRule> resourceAlarmRules, List<ResourceAlarmMessage> resourceAlarmMessageList) {
        long curTime = System.currentTimeMillis();
        long startTime = Optional.ofNullable(redisTemplate.opsForValue().get(START_TIME)).orElse(-1L);
        // 换算成毫秒值，resourceAlarmRules 都是同一个资源，同一个步长，取一个即可
        ResourceAlarmRule resourceAlarmRule = resourceAlarmRules.get(0);
        long timeDuration = resourceAlarmRule.getTimeDuration() * 1000;
        long diffTime = -1L == startTime ? 10000L : curTime - startTime;
        try {
            if (diffTime >= timeDuration) {
                MetricsQueryRange metricsQueryRange = getMetricsQueryRange(resourceAlarmRule.getResourceEnum(), curTime, timeDuration);
                List<NodeResourceVariationInfo> nodeResourceVariationInfos = resourceMonitorService.queryNodeResourceVariationInfo(metricsQueryRange);
                for (NodeResourceVariationInfo nodeResourceVariationInfo : nodeResourceVariationInfos) {
                    OptionalDouble average = nodeResourceVariationInfo.getVariationInfoList().stream().mapToDouble(Double::doubleValue).average();
                    // 按照同一个资源，同一个步长的规则进行比较即可，分别比较对应的阈值
                    for (ResourceAlarmRule rule : resourceAlarmRules) {
                        boolean condition = average.isPresent() && formatDouble(average.getAsDouble(), 2) > rule.getThreshold();
                        ResourceAlarmMessage resourceAlarmMessage = createResourceAlarmMessage(rule, nodeResourceVariationInfo.getNodeName());
                        resourceAlarmMessage.setAlarmStatus(condition ? ResourceAlarmConstants.ALARM_MESSAGE : ResourceAlarmConstants.NORMAL_MESSAGE);
                        resourceAlarmMessageList.add(resourceAlarmMessage);
                    }
                }
            }
        } catch (Exception e) {
            log.error("通过 Prometheus 查询数据，判断对应的资源告警是否满足条件异常，", e);
        }
    }

    private static MetricsQueryRange getMetricsQueryRange(ResourceEnum resourceEnum, long curTime, Long timeDuration) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        double timeStampWithDecimal = curTime / 1000.0;
        double startTimeStampWithDecimal = (curTime - timeDuration) / 1000.0;
        String start = decimalFormat.format(startTimeStampWithDecimal);
        String end = decimalFormat.format(timeStampWithDecimal);
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setResourceEnum(resourceEnum.name());
        metricsQueryRange.setStart(start);
        metricsQueryRange.setEnd(end);
        metricsQueryRange.setStep(2f);
        metricsQueryRange.setNodeName(PrometheusMetricsConstant.ALL_TAG);
        metricsQueryRange.setInstance(PrometheusMetricsConstant.ALL_TAG);
        return metricsQueryRange;
    }

    private ResourceAlarmMessage createResourceAlarmMessage(ResourceAlarmRule resourceAlarmRule, String nodeName) {
        String alarmMsg = String.format(resourceAlarmRule.getResourceEnum().getAlarmMessage(), nodeName, resourceAlarmRule.getThreshold());
        return ResourceAlarmMessage.builder()
                .message(alarmMsg).resourceEnum(resourceAlarmRule.getResourceEnum())
                .ruleId(resourceAlarmRule.getId()).tenantId(resourceAlarmRule.getTenantId())
                .alarmTime(DateUtil.date()).nodeName(nodeName)
                .userId(resourceAlarmRule.getUserId()).build();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 程序启动向 redis 写入一个程序启动完成的时间
        redisTemplate.opsForValue().set(START_TIME, System.currentTimeMillis());
    }

    public static double formatDouble(double num, int bit) {
        BigDecimal bd = BigDecimal.valueOf(num);
        BigDecimal result = bd.setScale(bit, RoundingMode.HALF_UP);
        return result.doubleValue();
    }
}
