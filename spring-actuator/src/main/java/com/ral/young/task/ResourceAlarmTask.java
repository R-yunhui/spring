package com.ral.young.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.ral.young.bo.MetricsQueryRange;
import com.ral.young.bo.NodeResourceVariationInfo;
import com.ral.young.bo.ResourceAlarmMessage;
import com.ral.young.bo.ResourceAlarmRule;
import com.ral.young.enums.ResourceEnum;
import com.ral.young.handler.WsMessageBroadcaster;
import com.ral.young.service.ResourceAlarmMessageService;
import com.ral.young.service.ResourceAlarmRuleService;
import com.ral.young.service.ResourceMonitorService;
import com.ral.young.service.impl.ResourceMonitorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
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

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1L,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(20), ThreadFactoryBuilder.create().setNamePrefix("resource-alarm-").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());


    public void dealAlarmTask() {
        List<ResourceAlarmRule> resourceAlarmRules = resourceAlarmRuleService.queryAllResourceAlarmRule();
        List<ResourceAlarmMessage> resourceAlarmMessageList = new ArrayList<>();
        for (ResourceAlarmRule resourceAlarmRule : resourceAlarmRules) {
            // 通过 Prometheus 查询数据，判断对应的资源告警是否满足条件
            if (check(resourceAlarmRule)) {
                ResourceAlarmMessage resourceAlarmMessage = createResourceAlarmMessage(resourceAlarmRule.getResourceEnum(),
                        resourceAlarmRule.getTenantId(), resourceAlarmRule.getId(), resourceAlarmRule.getThreshold());
                resourceAlarmMessageList.add(resourceAlarmMessage);

                // 广播告警
                wsMessageBroadcaster.broadcast(resourceAlarmMessage.getMessage());
            }
        }

        // 批量入库告警数据
        if (CollUtil.isNotEmpty(resourceAlarmMessageList)) {
            resourceAlarmMessageService.saveBatch(resourceAlarmMessageList);
        }
    }

    private boolean check(ResourceAlarmRule resourceAlarmRule) {
        Long timeDuration = resourceAlarmRule.getTimeDuration();
        long curTime = System.currentTimeMillis();
        MetricsQueryRange metricsQueryRange = getMetricsQueryRange(resourceAlarmRule, curTime, timeDuration);
        try {
            if (curTime - 10 >= timeDuration) {
                List<NodeResourceVariationInfo> nodeResourceVariationInfos = resourceMonitorService.queryNodeResourceVariationInfo(metricsQueryRange);
                if (CollUtil.isNotEmpty(nodeResourceVariationInfos)) {
                    for (NodeResourceVariationInfo nodeResourceVariationInfo : nodeResourceVariationInfos) {
                        OptionalDouble average = nodeResourceVariationInfo.getVariationInfoList().stream().mapToDouble(Double::doubleValue).average();
                        return average.isPresent() && average.getAsDouble() > (resourceAlarmRule.getThreshold() / 100);
                    }
                }
            }
        } catch (Exception e) {
            log.error("通过 Prometheus 采集数据校验是否超过用户设定的指标阈值失败,", e);
            // 暂时默认不进行告警
            return true;
        }
        return false;
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

    private ResourceAlarmMessage createResourceAlarmMessage(ResourceEnum resourceEnum, Long tenantId, Long ruleId, Double threshold) {
        String alarmMsg = String.format(resourceEnum.getAlarmMessage(), threshold);
        return ResourceAlarmMessage.builder().message(alarmMsg).resourceEnum(resourceEnum).ruleId(ruleId).tenantId(tenantId).alarmTime(DateUtil.date()).build();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(1000 * 60 * 10);
        dealAlarmTask();
    }
}
