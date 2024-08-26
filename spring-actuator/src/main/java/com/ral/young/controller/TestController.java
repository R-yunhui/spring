package com.ral.young.controller;

import com.ral.young.bo.*;
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

    @GetMapping(value = "/nodeStatus/{nodeName}/{start}/{end}")
    public List<ClusterNodeStatus> clusterNodeStatus(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryClusterNodeStatus(metricsQueryRange);
    }

    @GetMapping(value = "/memoryUsage/{nodeName}/{start}/{end}")
    public List<ClusterMemoryInfo> memoryUsage(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryMemoryUsage(metricsQueryRange);
    }

    @GetMapping(value = "/memoryUsageDetails/{nodeName}/{start}/{end}")
    public List<ClusterMemoryDetail> memoryUsageDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryMemoryUsageDetails(metricsQueryRange);
    }

    @GetMapping(value = "/diskUsage/{nodeName}/{start}/{end}")
    public List<ClusterDiskInfo> diskUsage(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryDiskUsage(metricsQueryRange);
    }

    @GetMapping(value = "/diskUsageDetails/{nodeName}/{start}/{end}")
    public List<ClusterDiskMemoryDetail> diskUsageDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryDiskUsageDetails(metricsQueryRange);
    }

    @GetMapping(value = "/cpuCore/{nodeName}/{start}/{end}")
    public List<ClusterCpuCoreInfo> cpuCore(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryCpuCore(metricsQueryRange);
    }

    @GetMapping(value = "/cpuCoreDetails/{nodeName}/{start}/{end}")
    public List<ClusterCpuCoreDetail> cpuCoreDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryCpuCoreDetails(metricsQueryRange);
    }

    @GetMapping(value = "/diskIoDetails/{nodeName}/{start}/{end}")
    public List<ClusterDiskIoDetail> diskIoDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryDiskIoDetails(metricsQueryRange);
    }

    @GetMapping(value = "/networkInfoDetails/{nodeName}/{start}/{end}")
    public List<ClusterNetworkDetail> networkInfoDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryNetworkInfoDetails(metricsQueryRange);
    }

    private static void extracted(MetricsQueryRange metricsQueryRange) {
        metricsQueryRange.setStep(10f);
        long timeMillis = System.currentTimeMillis();
        double timeStampWithDecimal = timeMillis / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String endTime = decimalFormat.format(timeStampWithDecimal);

        timeMillis = timeMillis - 1000 * 10 * 60;
        timeStampWithDecimal = timeMillis / 1000.0;
        String startTime = decimalFormat.format(timeStampWithDecimal);

        metricsQueryRange.setEnd(endTime);
        metricsQueryRange.setStart(startTime);
    }
}
