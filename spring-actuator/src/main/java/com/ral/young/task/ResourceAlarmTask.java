package com.ral.young.task;

import cn.hutool.core.collection.CollUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private WsMessageBroadcaster wsMessageBroadcaster;

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    private static final String START_TIME = "start::time";

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1L,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(20), ThreadFactoryBuilder.create().setNamePrefix("resource-alarm-").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        executor.prestartAllCoreThreads();
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void dealAlarmTask() {
        // todo 分布式锁
        log.info("===== 开始统计资源信息，判断是否超过了配置的阈值 =====");
        long start = System.currentTimeMillis();
        List<ResourceAlarmRule> resourceAlarmRules = resourceAlarmRuleService.queryAllResourceAlarmRule();
        List<ResourceAlarmMessage> resourceAlarmMessageList = new ArrayList<>();
        long startTime = Optional.ofNullable(redisTemplate.opsForValue().get(START_TIME)).orElse(-1L);
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        for (ResourceAlarmRule resourceAlarmRule : resourceAlarmRules) {
            // 通过 Prometheus 查询数据，判断对应的资源告警是否满足条件
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> doAlarmCheck(resourceAlarmRule, startTime, resourceAlarmMessageList), executor);
            completableFutures.add(future);
        }

        if (CollUtil.isNotEmpty(completableFutures)) {
            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).thenCompose(o -> {
                //  批量入库告警数据
                if (CollUtil.isNotEmpty(resourceAlarmMessageList)) {
                    log.info("===== 本次产生的告警数量：{} =====", resourceAlarmMessageList.size());
                    resourceAlarmMessageService.generatedAlarmList(resourceAlarmMessageList);
                    log.info("===== 统计资源信息完成，耗时：{}ms =====", (System.currentTimeMillis() - start));
                }
                return null;
            });
        }
    }

    private void doAlarmCheck(ResourceAlarmRule resourceAlarmRule, long startTime, List<ResourceAlarmMessage> resourceAlarmMessageList) {
        long curTime = System.currentTimeMillis();
        // 换算成毫秒值
        long timeDuration = resourceAlarmRule.getTimeDuration() * 1000;
        long diffTime = -1L == startTime ? 10000 : curTime - startTime;
        MetricsQueryRange metricsQueryRange = getMetricsQueryRange(resourceAlarmRule, curTime, timeDuration);
        try {
            if (diffTime >= timeDuration) {
                List<NodeResourceVariationInfo> nodeResourceVariationInfos = resourceMonitorService.queryNodeResourceVariationInfo(metricsQueryRange);
                if (CollUtil.isNotEmpty(nodeResourceVariationInfos)) {
                    for (NodeResourceVariationInfo nodeResourceVariationInfo : nodeResourceVariationInfos) {
                        OptionalDouble average = nodeResourceVariationInfo.getVariationInfoList().stream().mapToDouble(Double::doubleValue).average();
                        boolean condition = average.isPresent() && formatDouble(average.getAsDouble(), 2) > resourceAlarmRule.getThreshold();
                        if (condition) {
                            ResourceAlarmMessage resourceAlarmMessage = createResourceAlarmMessage(resourceAlarmRule, nodeResourceVariationInfo.getNodeName());
                            resourceAlarmMessageList.add(resourceAlarmMessage);

                            // 广播告警
                            wsMessageBroadcaster.broadcast(resourceAlarmMessage.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("通过 Prometheus 查询数据，判断对应的资源告警是否满足条件异常，", e);
        }
    }

    private static MetricsQueryRange getMetricsQueryRange(ResourceAlarmRule resourceAlarmRule, long curTime, Long timeDuration) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        double timeStampWithDecimal = curTime / 1000.0;
        double startTimeStampWithDecimal = (curTime - timeDuration) / 1000.0;
        String start = decimalFormat.format(startTimeStampWithDecimal);
        String end = decimalFormat.format(timeStampWithDecimal);
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setResourceEnum(resourceAlarmRule.getResourceEnum().name());
        metricsQueryRange.setStart(start);
        metricsQueryRange.setEnd(end);
        metricsQueryRange.setStep(2f);
        metricsQueryRange.setNodeName(ResourceMonitorServiceImpl.ALL_TAG);
        metricsQueryRange.setInstance(ResourceMonitorServiceImpl.ALL_TAG);
        return metricsQueryRange;
    }

    private ResourceAlarmMessage createResourceAlarmMessage(ResourceAlarmRule resourceAlarmRule, String nodeName) {
        String alarmMsg = String.format(resourceAlarmRule.getResourceEnum().getAlarmMessage(), nodeName, resourceAlarmRule.getThreshold());
        return ResourceAlarmMessage.builder()
                .message(alarmMsg).resourceEnum(resourceAlarmRule.getResourceEnum())
                .ruleId(resourceAlarmRule.getId()).tenantId(resourceAlarmRule.getTenantId())
                .alarmTime(DateUtil.date()).nodeName(nodeName).build();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 程序启动向 redis 写入一个程序启动完成的时间
        redisTemplate.opsForValue().set(START_TIME, System.currentTimeMillis());
        dealAlarmTask();
    }

    public static double formatDouble(double num, int bit) {
        BigDecimal bd = BigDecimal.valueOf(num);
        BigDecimal result = bd.setScale(bit, RoundingMode.HALF_UP);
        return result.doubleValue();
    }
}
