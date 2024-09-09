package com.ral.young.controller;

import com.ral.young.bo.*;
import com.ral.young.enums.TimeEnum;
import com.ral.young.service.ResourceMonitorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个TestController类
 * @date 2024-08-22 16-23-40
 * @since 1.0.0
 */
@RestController
public class TestController {

    @Resource
    private ResourceMonitorService resourceMonitorService;

    @GetMapping(value = "/api/v1/resource-monitor/clusterNodeInfo")
    public List<ClusterNodeInfo> clusterNodeInfo() {
        return resourceMonitorService.queryClusterNodeInfo();
    }

    @GetMapping(value = "/api/v1/resource-monitor/gpuInfo")
    public List<GpuInfo> queryGpuInfo() {
        return resourceMonitorService.queryGpuInfo();
    }

    @GetMapping(value = "/api/v1/resource-monitor/nodeStatus")
    public ClusterNodeStatus clusterNodeStatus() {
        return resourceMonitorService.queryClusterNodeStatus();
    }

    @GetMapping(value = "/api/v1/resource-monitor/nodeResourceInfo/{nodeName}/{instance}/{resource}")
    public List<NodeResourceInfo> nodeResourceInfo(@PathVariable(value = "nodeName") String nodeName,
                                                   @PathVariable(value = "instance") String instance,
                                                   @PathVariable(value = "resource") String resource) {
        return resourceMonitorService.queryNodeResourceInfo(nodeName, instance, resource);
    }

    @GetMapping(value = "/api/v1/resource-monitor/nodeResourceVariationInfo/{nodeName}/{instance}/{timeEnum}/{resource}")
    public List<NodeResourceVariationInfo> nodeResourceVariationInfo(@PathVariable(value = "nodeName") String nodeName,
                                                                     @PathVariable(value = "instance") String instance,
                                                                     @PathVariable(value = "timeEnum") String timeEnum,
                                                                     @PathVariable(value = "resource") String resource
    ) {
        MetricsQueryRange metricsQueryRange = getMetricsQueryRange(nodeName, instance, timeEnum, resource);
        return resourceMonitorService.queryNodeResourceVariationInfo(metricsQueryRange);
    }

    private static MetricsQueryRange getMetricsQueryRange(String nodeName, String instance, String timeEnum, String resource) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        metricsQueryRange.setInstance(instance);
        metricsQueryRange.setResourceEnum(resource);

        long timeMillis = System.currentTimeMillis();
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        double timeStampWithDecimal = timeMillis / 1000.0;
        metricsQueryRange.setEnd(decimalFormat.format(timeStampWithDecimal));
        metricsQueryRange.setStart(TimeEnum.valueOf(timeEnum).getNearlyTime());

        metricsQueryRange.setStep(10.0f);
        return metricsQueryRange;
    }
}
