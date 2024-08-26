package com.ral.young.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.ral.young.bo.*;
import com.ral.young.constant.PrometheusMetricsConstant;
import com.ral.young.service.ResourceMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author renyunhuiCluster node status
 * @description 这是一个AIPlatMetricsTask类
 * @date 2024-08-21 14-03-40
 * @since 1.0.0
 */
@Component
@Slf4j
public class ResourceMonitorServiceImpl implements ResourceMonitorService {

    private static final String PROMETHEUS_URL = "http://192.168.36:39090/api/v1/";

    private static final String PROMETHEUS_QUERY_RANGE_URL = "query_range?query=";

    private static final String NODE = "node";

    @Resource
    private RestTemplate restTemplate;

    private QueryRangeMetricsResult queryRangeFromPrometheus(MetricsQueryRange metricsQueryRange) {
        String metricsTag = metricsQueryRange.getMetricsTag();
        metricsTag = metricsTag.replace(PrometheusMetricsConstant.NODE_NAME_REPLACE_TAG, "all".equals(metricsQueryRange.getNodeName()) ? PrometheusMetricsConstant.ALL_NODE_NAME : metricsQueryRange.getNodeName());
        String url = PROMETHEUS_URL + PROMETHEUS_QUERY_RANGE_URL + metricsTag;
        StringBuilder urlBuilder = new StringBuilder(url);

        urlBuilder.append("&start=").append(metricsQueryRange.getStart());
        urlBuilder.append("&end=").append(metricsQueryRange.getEnd());
        urlBuilder.append("&step=").append(metricsQueryRange.getStep());

        log.info("queryRangeFromPrometheus 的 url：{}", urlBuilder);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(urlBuilder.toString()).build();
        ResponseEntity<QueryRangeMetricsResult> entity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, HttpEntity.EMPTY, QueryRangeMetricsResult.class);
        return entity.getBody();
    }

    @Override
    public List<ClusterNodeInfo> queryClusterNodeInfo() {
        return Collections.emptyList();
    }

    @Override
    public List<ClusterNodeStatus> queryClusterNodeStatus(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        // 所有节点数：sum(kube_node_info)
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_KUBE_NODE_INFO);
        QueryRangeMetricsResult allNodeInfoQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        // 不可用节点数：sum(kube_node_spec_unschedulable)
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_KUBE_NODE_SPEC_UNSCHEDULABLE);
        QueryRangeMetricsResult unschedulableNodeInfoQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        List<ClusterNodeStatus> clusterNodeStatusList = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(allNodeInfoQueryResult, unschedulableNodeInfoQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> nodeResult = allNodeInfoQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> unschedulableNodeResult = unschedulableNodeInfoQueryResult.getData().getResult();
            for (int i = 0, n = nodeResult.size(); i < n; i++) {
                double allNodeSize = nodeResult.get(i).getValues().stream().mapToDouble(list -> list.get(1)).average().orElse(0.00);
                double unschedulableNodeSize = unschedulableNodeResult.get(i).getValues().stream().mapToDouble(list -> list.get(1)).average().orElse(0.00);
                clusterNodeStatusList.add(ClusterNodeStatus.builder()
                        .allNode((int) allNodeSize)
                        .failNode((int) unschedulableNodeSize)
                        .readyNode((int) allNodeSize - (int) unschedulableNodeSize)
                        .nodeName(nodeResult.get(i).getMetric().getOrDefault(NODE, metricsQueryRange.getNodeName()))
                        .build());
            }
        }
        log.info("统计集群节点状态耗时：{} ms", System.currentTimeMillis() - start);
        return clusterNodeStatusList;
    }

    @Override
    public List<ClusterMemoryInfo> queryMemoryUsage(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        // 集群总内存：sum(kube_node_status_allocatable{origin_prometheus=~"",resource="memory", unit="byte", node=~"^.*$"})
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_KUBE_NODE_STATUS_ALLOCATABLE_MEMORY);
        QueryRangeMetricsResult allMemoryQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        // 集群已使用内存：sum(container_memory_working_set_bytes{origin_prometheus=~"",container!="",node=~"^.*$"})
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_MEMORY_WORKING_SET_BYTES);
        QueryRangeMetricsResult usageMemoryQueryResult = queryRangeFromPrometheus(metricsQueryRange);
        List<ClusterMemoryInfo> clusterMemoryInfos = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(allMemoryQueryResult, usageMemoryQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> allMemoryResult = allMemoryQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> usageMemoryResult = usageMemoryQueryResult.getData().getResult();
            for (int i = 0, n = allMemoryResult.size(); i < n; i++) {
                double allMemorySize = allMemoryResult.get(i).getValues().stream().mapToDouble(list -> list.get(1)).average().orElse(0.00);
                double usageMemorySize = usageMemoryResult.get(i).getValues().stream().mapToDouble(list -> list.get(1)).average().orElse(0.00);
                clusterMemoryInfos.add(ClusterMemoryInfo.builder()
                        .allMemorySize(allMemorySize / 1024 / 1024 / 1024)
                        .usageMemorySize(usageMemorySize / 1024 / 1024 / 1024)
                        .nodeName(allMemoryResult.get(i).getMetric().getOrDefault(NODE, metricsQueryRange.getNodeName()))
                        .build());
            }
        }

        log.info("统计集群节点内存使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterMemoryInfos;
    }

    @Override
    public List<ClusterMemoryDetail> queryMemoryUsageDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        // 集群节点内存使用率：sum(container_memory_working_set_bytes{origin_prometheus=~"",container!="",node=~"^.*$"})by (node) / sum(kube_node_status_allocatable{origin_prometheus=~"",resource="memory", unit="byte", node=~"^.*$"})by (node)*100
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_MEMORY_WORKING_SET_BYTES_DETAIL);
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<ClusterMemoryDetail> clusterMemoryDetails = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> {
                Map<String, String> metric = o.getMetric();
                if (null != metric && metric.containsKey(NODE)) {
                    String nodeName = metric.get(NODE);
                    List<List<Double>> values = o.getValues();
                    ClusterMemoryDetail clusterMemoryDetail = new ClusterMemoryDetail();
                    clusterMemoryDetail.setNodeName(nodeName);
                    Map<Long, Double> capCoreDetailMap = new HashMap<>(values.size());
                    values.forEach(value -> capCoreDetailMap.put((long) (value.get(0) * 1000), value.get(1)));
                    clusterMemoryDetail.setCapMemroyDetailMap(capCoreDetailMap);
                    clusterMemoryDetails.add(clusterMemoryDetail);
                }
            });
        }
        log.info("统计集群节点内存使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterMemoryDetails;
    }

    @Override
    public List<ClusterDiskInfo> queryDiskUsage(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        // 磁盘总量：sum(container_fs_limit_bytes{origin_prometheus=~"",device=~"^/dev/.*$",id="/",node=~"^.*$"})
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_LIMIT_BYTES);
        QueryRangeMetricsResult diskStorageQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        // 磁盘使用量：sum(container_fs_usage_bytes{origin_prometheus=~"",device=~"^/dev/.*$",id="/",node=~"^.*$"})
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_USAGE_BYTES);
        QueryRangeMetricsResult usageDiskStorageQueryResult = queryRangeFromPrometheus(metricsQueryRange);
        List<ClusterDiskInfo> clusterDiskInfos = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(diskStorageQueryResult, usageDiskStorageQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> allDiskStorageResult = diskStorageQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> usageDiskStorageResult = usageDiskStorageQueryResult.getData().getResult();
            for (int i = 0, n = allDiskStorageResult.size(); i < n; i++) {
                double allDiskStorageSize = allDiskStorageResult.get(i).getValues().stream().mapToDouble(list -> list.get(1)).average().orElse(0.00);
                double usageDiskStorageSize = usageDiskStorageResult.get(i).getValues().stream().mapToDouble(list -> list.get(1)).average().orElse(0.00);
                clusterDiskInfos.add(ClusterDiskInfo.builder()
                        .allDiskSize(allDiskStorageSize / 1024 / 1024 / 1024)
                        .usageDiskSize(usageDiskStorageSize / 1024 / 1024 / 1024)
                        .nodeName(allDiskStorageResult.get(i).getMetric().getOrDefault(NODE, metricsQueryRange.getNodeName()))
                        .build());
            }
        }

        log.info("统计集群磁盘使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterDiskInfos;
    }

    @Override
    public List<ClusterDiskMemoryDetail> queryDiskUsageDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        // 磁盘使用率：sum(container_fs_usage_bytes{origin_prometheus=~"",device=~"^/dev/.*$",id="/",node=~"^.*$"})by (node) / sum (container_fs_limit_bytes{origin_prometheus=~"",device=~"^/dev/.*$",id="/",node=~"^.*$"})by (node)
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_USAGE_BYTES_DETAIL);
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<ClusterDiskMemoryDetail> clusterDiskMemoryDetails = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> {
                Map<String, String> metric = o.getMetric();
                if (null != metric && metric.containsKey(NODE)) {
                    String nodeName = metric.get(NODE);
                    List<List<Double>> values = o.getValues();
                    ClusterDiskMemoryDetail clusterDiskMemoryDetail = new ClusterDiskMemoryDetail();
                    clusterDiskMemoryDetail.setNodeName(nodeName);
                    Map<Long, Double> capCoreDetailMap = new HashMap<>(values.size());
                    values.forEach(value -> capCoreDetailMap.put((long) (value.get(0) * 1000), value.get(1)));
                    clusterDiskMemoryDetail.setDiskDetailMap(capCoreDetailMap);
                    clusterDiskMemoryDetails.add(clusterDiskMemoryDetail);
                }
            });
        }
        log.info("统计集群磁盘使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterDiskMemoryDetails;
    }

    @Override
    public List<ClusterCpuCoreInfo> queryCpuCore(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        // cpu总核数：sum(kube_node_status_allocatable{origin_prometheus=~"",resource="cpu", unit="core", node=~"^.*$"})
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_KUBE_NODE_STATUS_ALLOCATABLE_CPU);
        QueryRangeMetricsResult allCpuCoreQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        // cpu核心数使用量：sum(irate(container_cpu_usage_seconds_total{origin_prometheus=~"",id="/",node=~"^.*$"}[2m]))
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL);
        QueryRangeMetricsResult usageCpuCoreQueryResult = queryRangeFromPrometheus(metricsQueryRange);
        List<ClusterCpuCoreInfo> clusterCpuCoreInfos = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(allCpuCoreQueryResult, usageCpuCoreQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> allCpuCoreResult = allCpuCoreQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> usageCpuCoreResult = usageCpuCoreQueryResult.getData().getResult();
            for (int i = 0, n = allCpuCoreResult.size(); i < n; i++) {
                double allCpuCoreSize = allCpuCoreResult.get(i).getValues().stream().mapToDouble(list -> list.get(1)).average().orElse(0.00);
                double usageCpuCoreSize = usageCpuCoreResult.get(i).getValues().stream().mapToDouble(list -> list.get(1)).average().orElse(0.00);
                clusterCpuCoreInfos.add(ClusterCpuCoreInfo.builder()
                        .cpuCoreSize((int) allCpuCoreSize)
                        .usageCpuCore(usageCpuCoreSize)
                        .nodeName(allCpuCoreResult.get(i).getMetric().getOrDefault(NODE, metricsQueryRange.getNodeName()))
                        .build());
            }
        }

        log.info("集群节点核心使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterCpuCoreInfos;
    }

    @Override
    public List<ClusterCpuCoreDetail> queryCpuCoreDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        // 集群节点 CPU 使用率：sum(irate(container_cpu_usage_seconds_total{origin_prometheus=~"",container!="",node=~"^.*$"}[2m]))by (node) / sum(kube_node_status_allocatable{origin_prometheus=~"",resource="cpu", unit="core", node=~"^.*$"})by (node)*100
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL_DETAIL);
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<ClusterCpuCoreDetail> clusterCpuCoreDetails = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> {
                Map<String, String> metric = o.getMetric();
                String nodeName = metric.getOrDefault(NODE, metricsQueryRange.getNodeName());
                List<List<Double>> values = o.getValues();
                ClusterCpuCoreDetail clusterCpuCoreDetail = new ClusterCpuCoreDetail();
                clusterCpuCoreDetail.setNodeName(nodeName);
                Map<Long, Double> capCoreDetailMap = new HashMap<>(values.size());
                values.forEach(value -> capCoreDetailMap.put((long) (value.get(0) * 1000), value.get(1)));
                clusterCpuCoreDetail.setCpuCoreDetailMap(capCoreDetailMap);
            });
        }
        log.info("统计集群CPU使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterCpuCoreDetails;
    }

    @Override
    public List<ClusterDiskIoDetail> queryDiskIoDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        List<ClusterDiskIoDetail> clusterDiskIoDetailList = new ArrayList<>();
        // 接收：sum(irate(container_fs_reads_bytes_total{origin_prometheus=~"",node=~"^.*$",namespace=~".*"}[2m]))*8
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_FS_READS_BYTES_TOTAL);
        QueryRangeMetricsResult acceptQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        // 发送：sum(irate(container_fs_write_bytes_total{origin_prometheus=~"",node=~"^.*$",namespace=~".*"}[2m]))*8
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_FS_WRITE_BYTES_TOTAL);
        QueryRangeMetricsResult sendQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        if (ObjectUtil.isAllNotEmpty(acceptQueryResult, sendQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> acceptResult = acceptQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> sendResult = sendQueryResult.getData().getResult();
            for (int i = 0, n = acceptResult.size(); i < n; i++) {
                QueryRangeMetricsResult.DataDTO.ResultDTO acceptResultDTO = acceptResult.get(i);
                QueryRangeMetricsResult.DataDTO.ResultDTO sendResultDTO = sendResult.get(i);
                Map<String, String> metric = acceptResultDTO.getMetric();
                String nodeName = metric.getOrDefault(NODE, metricsQueryRange.getNodeName());
                Map<Long, Double> sendBytes = new HashMap<>(acceptResultDTO.getValues().size());
                Map<Long, Double> acceptBytes = new HashMap<>(sendResultDTO.getValues().size());

                // 暂定 MB 为单位
                acceptResultDTO.getValues().forEach(value -> acceptBytes.put((long) (value.get(0) * 1000), value.get(1) / 1024 / 1024));
                sendResultDTO.getValues().forEach(value -> sendBytes.put((long) (value.get(0) * 1000), value.get(1) / 1024 / 1024));

                clusterDiskIoDetailList.add(ClusterDiskIoDetail.builder()
                        .nodeName(nodeName)
                        .receiveBytes(acceptBytes)
                        .sendBytes(sendBytes)
                        .build());
            }
        }
        log.info("查询集群节点磁盘IO情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterDiskIoDetailList;
    }

    @Override
    public List<ClusterNetworkDetail> queryNetworkInfoDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        List<ClusterNetworkDetail> clusterNetworkDetails = new ArrayList<>();
        // 接收：sum(irate(container_network_receive_bytes_total{origin_prometheus=~"",node=~"^.*$",namespace=~".*"}[2m]))*8
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_NETWORK_RECEIVE_BYTES_TOTAL_RECEIVE);
        QueryRangeMetricsResult acceptQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        // 发送：sum(irate(container_network_transmit_bytes_total{origin_prometheus=~"",node=~"^.*$",namespace=~".*"}[2m]))*8
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_NETWORK_TRANSMIT_BYTES_TOTAL_SEND);
        QueryRangeMetricsResult sendQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        if (ObjectUtil.isAllNotEmpty(acceptQueryResult, sendQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> acceptResult = acceptQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> sendResult = sendQueryResult.getData().getResult();
            for (int i = 0, n = acceptResult.size(); i < n; i++) {
                QueryRangeMetricsResult.DataDTO.ResultDTO acceptResultDTO = acceptResult.get(i);
                QueryRangeMetricsResult.DataDTO.ResultDTO sendResultDTO = sendResult.get(i);
                Map<String, String> metric = acceptResultDTO.getMetric();
                String nodeName = metric.getOrDefault(NODE, metricsQueryRange.getNodeName());
                Map<Long, Double> sendBytes = new HashMap<>(acceptResultDTO.getValues().size());
                Map<Long, Double> acceptBytes = new HashMap<>(sendResultDTO.getValues().size());

                // 暂定 MB 为单位
                acceptResultDTO.getValues().forEach(value -> acceptBytes.put((long) (value.get(0) * 1000), value.get(1) / 1024 / 1024));
                sendResultDTO.getValues().forEach(value -> sendBytes.put((long) (value.get(0) * 1000), value.get(1) / 1024 / 1024));

                clusterNetworkDetails.add(ClusterNetworkDetail.builder()
                        .nodeName(nodeName)
                        .receiveBytes(acceptBytes)
                        .sendBytes(sendBytes)
                        .build());
            }
        }
        log.info("查询集群节点网络使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterNetworkDetails;
    }
}
