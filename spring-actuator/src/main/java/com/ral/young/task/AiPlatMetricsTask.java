package com.ral.young.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.ral.young.bo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author renyunhuiCluster node status
 * @description 这是一个AIPlatMetricsTask类
 * @date 2024-08-21 14-03-40
 * @since 1.0.0
 */
@Component
@Slf4j
public class AiPlatMetricsTask implements ApplicationRunner {

    private static final String PROMETHEUS_URL = "http://10.10.1.103:39090/api/v1/";

    private static final String PROMETHEUS_QUERY_RANGE_URL = "query_range?query=";

    private static final String PROMETHEUS_QUERY_URL = "query?query=";

    @Resource
    private RestTemplate restTemplate;

    public void queryClusterNodeStatus() {
        long start = System.currentTimeMillis();
        // 所有节点数：sum(kube_node_info)
        MetricsQuery metricsQuery = buildMetricQuery("sum(kube_node_info)");

        // 不可用节点数：sum(kube_node_spec_unschedulable)
        QueryMetricsResult allNodeInfo = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag("sum(kube_node_spec_unschedulable)");
        QueryMetricsResult unschedulableNodeInfo = queryFromPrometheus(metricsQuery);

        if (ObjectUtil.isAllNotEmpty(allNodeInfo, unschedulableNodeInfo)) {
            Double allNodeSize = allNodeInfo.getData().getResult().get(0).getValue().get(1);
            Double unschedulableNodeSize = unschedulableNodeInfo.getData().getResult().get(0).getValue().get(1);

            ClusterNodeStatus clusterNodeStatus = ClusterNodeStatus.builder()
                    .allNode(allNodeSize.intValue())
                    .failNode(unschedulableNodeSize.intValue())
                    .readyNode(allNodeSize.intValue() - unschedulableNodeSize.intValue())
                    .build();
            log.info("集群节点状态：{}", clusterNodeStatus);
        }
        log.info("统计集群节点状态耗时：{} ms", System.currentTimeMillis() - start);
    }

    public void queryMemoryUsage() {
        long start = System.currentTimeMillis();
        // 集群总内存：sum(kube_node_status_allocatable{origin_prometheus=~"",resource="memory", unit="byte", node=~"^.*$"})
        MetricsQuery allMemory = buildMetricQuery("sum(kube_node_status_allocatable{origin_prometheus=~\"\",resource=\"memory\", unit=\"byte\", node=~\"^.*$\"})");
        QueryMetricsResult allMemoryResult = queryFromPrometheus(allMemory);

        // 集群已使用内存：sum(container_memory_working_set_bytes{origin_prometheus=~"",container!="",node=~"^.*$"})
        MetricsQuery usageMemory = buildMetricQuery("sum(container_memory_working_set_bytes{origin_prometheus=~\"\",container!=\"\",node=~\"^.*$\"})");
        QueryMetricsResult usageMemoryResult = queryFromPrometheus(usageMemory);
        if (ObjectUtil.isAllNotEmpty(allMemoryResult, usageMemoryResult)) {
            List<ClusterMemoryInfo> clusterMemoryInfos = new ArrayList<>();
            List<Double> allResult = allMemoryResult.getData().getResult().get(0).getValue();
            List<Double> usageResult = usageMemoryResult.getData().getResult().get(0).getValue();
            clusterMemoryInfos.add(ClusterMemoryInfo.builder()
                    .nodeName("all")
                    .timestamp((long) (allResult.get(0) * 1000L))
                    .time(DateUtil.format(DateUtil.date((long) (allResult.get(0) * 1000L)), "yyyy-MM-dd HH:mm:ss"))
                    .allMemorySize(allResult.get(1) / 1024 / 1024 / 1024)
                    .usageMemorySize(usageResult.get(1) / 1024 / 1024 / 1024)
                    .build());
            log.info("集群节点内存使用情况：{}", JSONUtil.toJsonStr(clusterMemoryInfos));
        }

        log.info("统计集群节点内存使用情况耗时：{} ms", System.currentTimeMillis() - start);
    }

    public void queryDiskUsage() {
        long start = System.currentTimeMillis();
        // 磁盘总量：sum(container_fs_limit_bytes{origin_prometheus=~"",device=~"^/dev/.*$",id="/",node=~"^.*$"})
        MetricsQuery allDiskStorage = buildMetricQuery("sum(container_fs_limit_bytes{origin_prometheus=~\"\",device=~\"^/dev/.*$\",id=\"/\",node=~\"^.*$\"})");
        QueryMetricsResult allDiskStorageResult = queryFromPrometheus(allDiskStorage);

        // 磁盘使用量：sum(container_fs_usage_bytes{origin_prometheus=~"",device=~"^/dev/.*$",id="/",node=~"^.*$"})
        MetricsQuery usageDiskStorage = buildMetricQuery("sum(container_fs_usage_bytes{origin_prometheus=~\"\",device=~\"^/dev/.*$\",id=\"/\",node=~\"^.*$\"})");
        QueryMetricsResult usageDiskStorageResult = queryFromPrometheus(usageDiskStorage);
        if (ObjectUtil.isAllNotEmpty(allDiskStorageResult, usageDiskStorageResult)) {
            List<ClusterDiskInfo> clusterDiskInfos = new ArrayList<>();
            List<Double> allResult = allDiskStorageResult.getData().getResult().get(0).getValue();
            List<Double> usageResult = usageDiskStorageResult.getData().getResult().get(0).getValue();
            clusterDiskInfos.add(ClusterDiskInfo.builder()
                    .timestamp((long) (allResult.get(0) * 1000L))
                    .time(DateUtil.format(DateUtil.date((long) (allResult.get(0) * 1000L)), "yyyy-MM-dd HH:mm:ss"))
                    .allDiskSize(allResult.get(1) / 1024 / 1024 / 1024)
                    .usageDiskSize(usageResult.get(1) / 1024 / 1024 / 1024)
                    .build());
            log.info("集群磁盘使用情况：{}", JSONUtil.toJsonStr(clusterDiskInfos));
        }

        log.info("统计集群磁盘使用情况耗时：{} ms", System.currentTimeMillis() - start);
    }

    public void queryMemoryUsageRange() {
        long start = System.currentTimeMillis();
        // 集群节点内存使用率：sum(container_memory_working_set_bytes{origin_prometheus=~"",container!="",node=~"^.*$"}) / sum(kube_node_status_allocatable{origin_prometheus=~"",resource="memory", unit="byte", node=~"^.*$"})*100
        MetricsQueryRange metricsQueryRange = buildMetricQueryRange("sum(container_memory_working_set_bytes{origin_prometheus=~\"\",container!=\"\",node=~\"^.*$\"}) / sum(kube_node_status_allocatable{origin_prometheus=~\"\",resource=\"memory\", unit=\"byte\", node=~\"^.*$\"})*100");
        queryRangeFromPrometheus(metricsQueryRange);
        log.info("统计集群节点内存使用率情况耗时：{} ms", System.currentTimeMillis() - start);
    }

    public void queryDiskUsageRange() {
        long start = System.currentTimeMillis();
        // 磁盘使用率：sum(container_fs_usage_bytes{origin_prometheus=~"",device=~"^/dev/.*$",id="/",node=~"^.*$"}) / sum (container_fs_limit_bytes{origin_prometheus=~"",device=~"^/dev/.*$",id="/",node=~"^.*$"})
        MetricsQueryRange metricsQueryRange = buildMetricQueryRange("sum(container_fs_usage_bytes{origin_prometheus=~\"\",device=~\"^/dev/.*$\",id=\"/\",node=~\"^.*$\"}) / sum (container_fs_limit_bytes{origin_prometheus=~\"\",device=~\"^/dev/.*$\",id=\"/\",node=~\"^.*$\"})");
        queryRangeFromPrometheus(metricsQueryRange);
        log.info("统计集群磁盘使用率情况耗时：{} ms", System.currentTimeMillis() - start);
    }

    public void queryCpuUsageRange() {
        long start = System.currentTimeMillis();
        // 集群节点内存使用率：sum(irate(container_cpu_usage_seconds_total{origin_prometheus=~"",container!="",node=~"^.*$"}[2m])) / sum(kube_node_status_allocatable{origin_prometheus=~"",resource="cpu", unit="core", node=~"^.*$"})*100
        MetricsQueryRange metricsQueryRange = buildMetricQueryRange("sum(irate(container_cpu_usage_seconds_total{origin_prometheus=~\"\",container!=\"\",node=~\"^.*$\"}[2m])) / sum(kube_node_status_allocatable{origin_prometheus=~\"\",resource=\"cpu\", unit=\"core\", node=~\"^.*$\"})*100");
        queryRangeFromPrometheus(metricsQueryRange);
        log.info("统计集群节点内存使用率情况耗时：{} ms", System.currentTimeMillis() - start);
    }

    public void queryCpuCore() {
        long start = System.currentTimeMillis();
        // cpu总核数：sum(kube_node_status_allocatable{origin_prometheus=~"",resource="cpu", unit="core", node=~"^.*$"})
        MetricsQuery allCpuCore = buildMetricQuery("sum(kube_node_status_allocatable{origin_prometheus=~\"\",resource=\"cpu\", unit=\"core\", node=~\"^.*$\"})");
        QueryMetricsResult allCpuCoreResult = queryFromPrometheus(allCpuCore);

        // cpu核心数使用量：sum(irate(container_cpu_usage_seconds_total{origin_prometheus=~"",id="/",node=~"^.*$"}[2m]))
        MetricsQuery usageCpuCore = buildMetricQuery("sum(irate(container_cpu_usage_seconds_total{origin_prometheus=~\"\",id=\"/\",node=~\"^.*$\"}[2m]))");
        QueryMetricsResult usageCpuCoreResult = queryFromPrometheus(usageCpuCore);
        if (ObjectUtil.isAllNotEmpty(allCpuCoreResult, usageCpuCoreResult)) {
            List<ClusterCpuCoreInfo> clusterCpuCoreInfos = new ArrayList<>();
            List<Double> allResult = allCpuCoreResult.getData().getResult().get(0).getValue();
            List<Double> usageResult = usageCpuCoreResult.getData().getResult().get(0).getValue();
            clusterCpuCoreInfos.add(ClusterCpuCoreInfo.builder()
                    .nodeName("all")
                    .timestamp((long) (allResult.get(0) * 1000L))
                    .time(DateUtil.format(DateUtil.date((long) (allResult.get(0) * 1000L)), "yyyy-MM-dd HH:mm:ss"))
                    .cpuCoreSize(allResult.get(1).intValue())
                    .usageCpuCore(usageResult.get(1))
                    .build());
            log.info("集群节点核心使用情况：{}", JSONUtil.toJsonStr(clusterCpuCoreInfos));
        }

        log.info("集群节点核心使用情况耗时：{} ms", System.currentTimeMillis() - start);
    }

    public void queryNetworkInfo() {
        long start = System.currentTimeMillis();
        // 接收：sum(irate(container_network_receive_bytes_total{origin_prometheus=~"",node=~"^.*$",namespace=~".*"}[2m]))*8
        MetricsQueryRange acceptQuery = buildMetricQueryRange("sum(container_network_receive_bytes_total{origin_prometheus=~\"\",node=~\"^.*$\",namespace=~\".*\"})");
        queryRangeFromPrometheus(acceptQuery);

        // 发送：sum(irate(container_network_transmit_bytes_total{origin_prometheus=~"",node=~"^.*$",namespace=~".*"}[2m]))*8
        MetricsQueryRange sendQuery = buildMetricQueryRange("sum(container_cpu_usage_seconds_total{origin_prometheus=~\"\",id=\"/\",node=~\"^.*$\"})");
        queryRangeFromPrometheus(sendQuery);
        log.info("查询集群节点网络使用情况耗时：{} ms", System.currentTimeMillis() - start);
    }

    private static MetricsQueryRange buildMetricQueryRange(String metrics) {
        MetricsQueryRange metricsQueryRange = new MetricsQueryRange();
        metricsQueryRange.setMetricsTag(metrics);
        long timeMillis = System.currentTimeMillis();
        double timeStampWithDecimal = timeMillis / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String endTime = decimalFormat.format(timeStampWithDecimal);

        timeMillis = timeMillis - 1000 * 10 * 60;
        timeStampWithDecimal = timeMillis / 1000.0;
        String startTime = decimalFormat.format(timeStampWithDecimal);

        metricsQueryRange.setStart(startTime);
        metricsQueryRange.setEnd(endTime);
        metricsQueryRange.setStep(10f);
        return metricsQueryRange;
    }

    private static MetricsQuery buildMetricQuery(String metrics) {
        MetricsQuery metricsQuery = new MetricsQuery();
        metricsQuery.setMetricsTag(metrics);

        long timeMillis = System.currentTimeMillis();
        double timeStampWithDecimal = timeMillis / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String endTime = decimalFormat.format(timeStampWithDecimal);

        metricsQuery.setDateTime(endTime);
        return metricsQuery;
    }

    private QueryMetricsResult queryFromPrometheus(MetricsQuery metricsQuery) {
        String metricsTag = metricsQuery.getMetricsTag();
        String url = PROMETHEUS_URL + PROMETHEUS_QUERY_URL + metricsTag;
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("&time=").append(metricsQuery.getDateTime());

        log.info("本次 queryFromPrometheus 的 url：{}", urlBuilder);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(urlBuilder.toString()).build();
        ResponseEntity<QueryMetricsResult> entity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, HttpEntity.EMPTY, QueryMetricsResult.class);
        QueryMetricsResult body = entity.getBody();
        log.info("queryFromPrometheus 的结果：{}", JSONUtil.toJsonStr(body));
        return body;
    }

    private QueryRangeMetricsResult queryRangeFromPrometheus(MetricsQueryRange metricsQueryRange) {
        String metricsTag = metricsQueryRange.getMetricsTag();
        String url = PROMETHEUS_URL + PROMETHEUS_QUERY_RANGE_URL + metricsTag;
        StringBuilder urlBuilder = new StringBuilder(url);

        urlBuilder.append("&start=").append(metricsQueryRange.getStart());
        urlBuilder.append("&end=").append(metricsQueryRange.getEnd());
        urlBuilder.append("&step=").append(metricsQueryRange.getStep());

        log.info("本次 queryRangeFromPrometheus 的 url：{}", urlBuilder);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(urlBuilder.toString()).build();
        ResponseEntity<QueryRangeMetricsResult> entity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, HttpEntity.EMPTY, QueryRangeMetricsResult.class);
        QueryRangeMetricsResult body = entity.getBody();
        log.info("queryRangeFromPrometheus 的结果：{}", JSONUtil.toJsonStr(body));
        return body;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        queryClusterNodeStatus();

        queryMemoryUsage();

        queryCpuCore();

        queryMemoryUsageRange();

        queryCpuUsageRange();

        queryDiskUsage();

        queryDiskUsageRange();

        queryNetworkInfo();
    }
}
