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

    @GetMapping(value = "/clusterNodeInfo")
    public List<ClusterNodeInfo> clusterNodeInfo() {
        return resourceMonitorService.queryClusterNodeInfo();
    }

    @GetMapping(value = "/gpuInfo/{nodeName}")
    public List<GpuInfo> gpuInfo(@PathVariable(value = "nodeName") String nodeName) {
        return resourceMonitorService.queryGpuInfo(nodeName);
    }

    @GetMapping(value = "/nodeStatus")
    public ClusterNodeStatus clusterNodeStatus() {
        return resourceMonitorService.queryClusterNodeStatus();
    }

    @GetMapping(value = "/cpuCore/{nodeName}/{instance}")
    public List<ClusterCpuCoreInfo> cpuCore(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "instance") String instance) {
        return resourceMonitorService.queryCpuCore(nodeName, instance);
    }

    @GetMapping(value = "/cpuCoreDetails/{nodeName}/{instance}/{start}/{end}")
    public List<ClusterCpuCoreDetail> cpuCoreDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "instance") String instance, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        metricsQueryRange.setInstance(instance);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryCpuCoreDetails(metricsQueryRange);
    }

    @GetMapping(value = "/memoryUsage/{nodeName}/{instance}")
    public List<ClusterMemoryInfo> memoryUsage(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "instance") String instance) {
        return resourceMonitorService.queryMemoryUsage(nodeName, instance);
    }

    @GetMapping(value = "/memoryUsageDetails/{nodeName}/{instance}/{start}/{end}")
    public List<ClusterMemoryDetail> memoryUsageDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "instance") String instance, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        metricsQueryRange.setInstance(instance);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryMemoryUsageDetails(metricsQueryRange);
    }

    @GetMapping(value = "/gpuMemoryInfo/{nodeName}")
    public List<GpuMemoryInfo> gpuMemoryInfo(@PathVariable(value = "nodeName") String nodeName) {
        return resourceMonitorService.queryGpuMemoryInfo(nodeName);
    }

    @GetMapping(value = "/gpuMemoryDetails/{nodeName}/{start}/{end}")
    public List<GpuMemoryDetail> gpuMemoryDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryGpuMemoryDetails(metricsQueryRange);
    }

    @GetMapping(value = "/diskUsage/{nodeName}/{instance}")
    public List<ClusterDiskInfo> diskUsage(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "instance") String instance) {
        return resourceMonitorService.queryDiskUsage(nodeName, instance);
    }

    @GetMapping(value = "/diskUsageDetails/{nodeName}/{instance}/{start}/{end}")
    public List<ClusterDiskMemoryDetail> diskUsageDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "instance") String instance, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        metricsQueryRange.setInstance(instance);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryDiskUsageDetails(metricsQueryRange);
    }

    @GetMapping(value = "/diskIoDetails/{nodeName}/{instance}/{start}/{end}")
    public List<ClusterDiskIoDetail> diskIoDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "instance") String instance, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        metricsQueryRange.setInstance(instance);
        extracted(metricsQueryRange);
        return resourceMonitorService.queryDiskIoDetails(metricsQueryRange);
    }

    @GetMapping(value = "/networkInfoDetails/{nodeName}/{instance}/{start}/{end}")
    public List<ClusterNetworkDetail> networkInfoDetails(@PathVariable(value = "nodeName") String nodeName, @PathVariable(value = "instance") String instance, @PathVariable(value = "start") String start, @PathVariable(value = "end") String end) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setNodeName(nodeName);
        metricsQueryRange.setInstance(instance);
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
        metricsQueryRange.setStart(TimeEnum.NEARLY_ONE_HOUR.getNearlyTime());
    }
}
