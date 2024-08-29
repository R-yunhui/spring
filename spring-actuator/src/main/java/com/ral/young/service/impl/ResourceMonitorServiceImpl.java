package com.ral.young.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.ral.young.bo.*;
import com.ral.young.constant.PrometheusMetricsConstant;
import com.ral.young.service.ResourceMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
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

    private static final String PROMETHEUS_URL = "http://192.168.2.36:39090/api/v1/";

    private static final String PROMETHEUS_QUERY_RANGE_URL = "query_range?query=";

    private static final String PROMETHEUS_QUERY_URL = "query?query=";

    private static final String NODE = "node";

    private static final String NODE_NAME_TAG = "nodename";

    private static final String ALL_TAG = "all";

    private static final String INSTANCE_TAG = "instance";

    private static final String GPU_TAG = "gpu";

    private static final String HOST_NAME_TAG = "Hostname";

    private static final String PLUS_TAG = "+";

    @Resource
    private RestTemplate restTemplate;

    private QueryMetricsResult queryFromPrometheus(MetricsQuery metricsQuery) {
        StringBuilder urlBuilder = replaceMetricsTag(metricsQuery.getMetricsTag(), metricsQuery.getNodeName(), metricsQuery.getInstance(), PROMETHEUS_QUERY_URL);
        urlBuilder.append("&time=").append(metricsQuery.getDateTime());

        log.info("queryFromPrometheus 的 url：{}", urlBuilder);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(urlBuilder.toString()).build();
        ResponseEntity<QueryMetricsResult> entity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, HttpEntity.EMPTY, QueryMetricsResult.class);
        return entity.getBody();
    }

    private StringBuilder replaceMetricsTag(String metricsTag, String nodeName, String instance, String prometheusQueryUrl) {
        if (StringUtils.isNotBlank(nodeName)) {
            metricsTag = metricsTag.replace(PrometheusMetricsConstant.NODE_NAME_REPLACE_TAG, nodeName.equals(ALL_TAG) ? PrometheusMetricsConstant.ALL_NODE_NAME : nodeName);
        }

        if (StringUtils.isNotBlank(instance)) {
            metricsTag = metricsTag.replace(PrometheusMetricsConstant.INSTANCE_NAME_REPLACE_TAG, instance.equals(ALL_TAG) ? PrometheusMetricsConstant.ALL_NODE_NAME : instance);
        }

        if (metricsTag.contains(PLUS_TAG)) {
            try {
                metricsTag = URLEncoder.encode(metricsTag, "UTF-8");
            } catch (Exception e) {
                log.error("处理异常", e);
            }
        }

        String url = PROMETHEUS_URL + prometheusQueryUrl + metricsTag;
        return new StringBuilder(url);
    }


    private QueryRangeMetricsResult queryRangeFromPrometheus(MetricsQueryRange metricsQueryRange) {
        StringBuilder urlBuilder = replaceMetricsTag(metricsQueryRange.getMetricsTag(), metricsQueryRange.getNodeName(), metricsQueryRange.getInstance(), PROMETHEUS_QUERY_RANGE_URL);
        urlBuilder.append("&start=").append(metricsQueryRange.getStart());
        urlBuilder.append("&end=").append(metricsQueryRange.getEnd());
        urlBuilder.append("&step=").append(metricsQueryRange.getStep());

        log.info("queryRangeFromPrometheus 的 url：{}", urlBuilder);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(urlBuilder.toString()).build(metricsQueryRange.isSpecific());
        ResponseEntity<QueryRangeMetricsResult> entity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, HttpEntity.EMPTY, QueryRangeMetricsResult.class);
        return entity.getBody();
    }

    @Override
    public List<ClusterNodeInfo> queryClusterNodeInfo() {
        MetricsQuery metricsQuery = new MetricsQuery();
        metricsQuery.setDateTime(getCurDateTime());
        metricsQuery.setMetricsTag(PrometheusMetricsConstant.NODE_UNAME_INFO);
        QueryMetricsResult queryMetricsResult = queryFromPrometheus(metricsQuery);
        List<ClusterNodeInfo> clusterNodeInfoList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(queryMetricsResult)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> result = queryMetricsResult.getData().getResult();
            for (QueryMetricsResult.DataDTO.ResultDTO resultDTO : result) {
                Map<String, String> metric = resultDTO.getMetric();
                clusterNodeInfoList.add(ClusterNodeInfo.builder().nodeInstance(metric.get(INSTANCE_TAG)).nodeName(metric.get(NODE_NAME_TAG)).build());
            }
        }
        return clusterNodeInfoList;
    }

    @Override
    public List<GpuInfo> queryGpuInfo(String nodeName) {
        MetricsQuery metricsQuery = new MetricsQuery();
        metricsQuery.setDateTime(getCurDateTime());
        metricsQuery.setNodeName("all");
        metricsQuery.setInstance("all");
        metricsQuery.setMetricsTag(PrometheusMetricsConstant.DCGM_GPU_USED_MEMORY);
        QueryMetricsResult usedMemoryResult = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.DCGM_GPU_FREE_MEMORY);
        QueryMetricsResult freeMemoryResult = queryFromPrometheus(metricsQuery);

        List<GpuInfo> gpuInfoList = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(usedMemoryResult, freeMemoryResult)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> result = usedMemoryResult.getData().getResult();
            for (int i = 0, n = result.size(); i < n; i++) {
                Map<String, String> metric = result.get(i).getMetric();
                List<Double> usedMemoryValue = usedMemoryResult.getData().getResult().get(i).getValue();
                List<Double> freeMemoryValue = freeMemoryResult.getData().getResult().get(i).getValue();
                double gpuMemorySize = formatDouble(freeMemoryValue.get(1) + usedMemoryValue.get(1));
                double gpuMemoryUsed = formatDouble(usedMemoryValue.get(1));
                double gpuUtilizationRate = formatDouble(gpuMemoryUsed / gpuMemorySize);

                 nodeName = metric.get(HOST_NAME_TAG);
                String finalNodeName = nodeName;
                Optional<GpuInfo> first = gpuInfoList.stream().filter(o -> finalNodeName.equals(o.getNodeName())).findFirst();
                if (first.isPresent()) {
                    first.get().getGpuCardInfos().add(
                            GpuInfo.GpuCardInfo.builder()
                                    .gpuIndex(metric.get(GPU_TAG))
                                    .gpuUtilizationRate(gpuUtilizationRate)
                                    .gpuMemorySize(gpuMemorySize)
                                    .gpuMemoryUsed(gpuMemoryUsed)
                                    .canSelect(gpuUtilizationRate > 0).build()
                    );
                } else {
                    List<GpuInfo.GpuCardInfo> gpuCardInfos = new ArrayList<>();
                    gpuCardInfos.add(GpuInfo.GpuCardInfo.builder()
                            .gpuIndex(metric.get(GPU_TAG))
                            .gpuUtilizationRate(gpuUtilizationRate)
                            .gpuMemorySize(gpuMemorySize)
                            .gpuMemoryUsed(gpuMemoryUsed)
                            .canSelect(gpuUtilizationRate > 0).build());

                    gpuInfoList.add(GpuInfo.builder()
                            .nodeName(metric.get(HOST_NAME_TAG))
                            .instance(metric.get(INSTANCE_TAG))
                            .gpuCardInfos(gpuCardInfos)
                            .build()
                    );
                }
            }
        }
        return gpuInfoList;
    }

    @Override
    public ClusterNodeStatus queryClusterNodeStatus() {
        long start = System.currentTimeMillis();
        MetricsQuery metricsQuery = new MetricsQuery();
        metricsQuery.setMetricsTag(PrometheusMetricsConstant.SUM_KUBE_NODE_INFO);
        metricsQuery.setDateTime(getCurDateTime());
        QueryMetricsResult allNodeInfoQueryResult = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.SUM_KUBE_NODE_SPEC_UNSCHEDULABLE);
        QueryMetricsResult unschedulableNodeInfoQueryResult = queryFromPrometheus(metricsQuery);

        ClusterNodeStatus clusterNodeStatus = null;
        if (ObjectUtil.isAllNotEmpty(allNodeInfoQueryResult, unschedulableNodeInfoQueryResult)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> nodeResult = allNodeInfoQueryResult.getData().getResult();
            List<QueryMetricsResult.DataDTO.ResultDTO> unschedulableNodeResult = unschedulableNodeInfoQueryResult.getData().getResult();
            for (int i = 0, n = nodeResult.size(); i < n; i++) {
                double allNodeSize = CollectionUtils.isEmpty(nodeResult.get(i).getValue()) ? 0 : nodeResult.get(i).getValue().get(1);
                double unschedulableNodeSize = CollectionUtils.isEmpty(unschedulableNodeResult.get(i).getValue()) ? 0 : unschedulableNodeResult.get(i).getValue().get(1);
                clusterNodeStatus = ClusterNodeStatus.builder().allNode((int) allNodeSize).failNode((int) unschedulableNodeSize).readyNode((int) allNodeSize - (int) unschedulableNodeSize).build();
            }
        }
        log.info("统计集群节点状态耗时：{} ms", System.currentTimeMillis() - start);
        return clusterNodeStatus;
    }

    @Override
    public List<ClusterMemoryInfo> queryMemoryUsage(String nodeName, String instance) {
        long start = System.currentTimeMillis();
        MetricsQuery metricsQuery = MetricsQuery.builder().metricsTag(PrometheusMetricsConstant.SUM_NODE_TOTAL_MEMORY).dateTime(getCurDateTime()).nodeName(nodeName).instance(instance).build();
        QueryMetricsResult allMemoryQueryResult = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.SUM_NODE_FREE_MEMORY);
        QueryMetricsResult usageMemoryQueryResult = queryFromPrometheus(metricsQuery);
        List<ClusterMemoryInfo> clusterMemoryInfos = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(allMemoryQueryResult, usageMemoryQueryResult)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> allMemoryResult = allMemoryQueryResult.getData().getResult();
            List<QueryMetricsResult.DataDTO.ResultDTO> usageMemoryResult = usageMemoryQueryResult.getData().getResult();
            for (int i = 0, n = allMemoryResult.size(); i < n; i++) {
                double allMemorySize = allMemoryResult.get(i).getValue().get(1);
                double usageMemorySize = usageMemoryResult.get(i).getValue().get(1);
                // 单位暂定为 GB
                clusterMemoryInfos.add(ClusterMemoryInfo.builder().allMemorySize(formatDouble(allMemorySize / 1024 / 1024 / 1024))
                        .usageMemorySize(formatDouble(usageMemorySize / 1024 / 1024 / 1024))
                        .nodeName(nodeName)
                        .instance(instance).build());
            }
        }

        log.info("统计集群节点内存使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterMemoryInfos;
    }

    @Override
    public List<ClusterMemoryDetail> queryMemoryUsageDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NODE_MEMORY_USED_RATE);
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<ClusterMemoryDetail> clusterMemoryDetails = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> {
                Map<String, String> metric = o.getMetric();
                if (null != metric) {
                    List<List<Double>> values = o.getValues();
                    ClusterMemoryDetail clusterMemoryDetail = new ClusterMemoryDetail();
                    clusterMemoryDetail.setNodeName(metricsQueryRange.getNodeName());
                    clusterMemoryDetail.setInstance(metricsQueryRange.getInstance());
                    Map<Long, String> memoryUsageMap = new HashMap<>(values.size());
                    values.forEach(value -> memoryUsageMap.put((long) (value.get(0) * 1000), formatDouble(value.get(1)) + "%"));
                    clusterMemoryDetail.setCapMemroyDetailMap(memoryUsageMap);
                    clusterMemoryDetails.add(clusterMemoryDetail);
                }
            });
        }
        log.info("统计集群节点内存使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterMemoryDetails;
    }

    @Override
    public List<ClusterDiskInfo> queryDiskUsage(String nodeName, String instance) {
        long start = System.currentTimeMillis();
        MetricsQuery metricsQuery = MetricsQuery.builder().metricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_LIMIT_BYTES).dateTime(getCurDateTime()).nodeName(nodeName).instance(instance).build();
        QueryMetricsResult diskStorageQueryResult = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_USAGE_BYTES);
        QueryMetricsResult usageDiskStorageQueryResult = queryFromPrometheus(metricsQuery);
        List<ClusterDiskInfo> clusterDiskInfos = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(diskStorageQueryResult, usageDiskStorageQueryResult)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> allDiskStorageResult = diskStorageQueryResult.getData().getResult();
            List<QueryMetricsResult.DataDTO.ResultDTO> usageDiskStorageResult = usageDiskStorageQueryResult.getData().getResult();
            for (int i = 0, n = allDiskStorageResult.size(); i < n; i++) {
                double allDiskStorageSize = allDiskStorageResult.get(i).getValue().get(1);
                double usageDiskStorageSize = usageDiskStorageResult.get(i).getValue().get(1);
                clusterDiskInfos.add(ClusterDiskInfo.builder().allDiskSize(formatDouble(allDiskStorageSize / 1024 / 1024 / 1024))
                        .usageDiskSize(formatDouble(usageDiskStorageSize / 1024 / 1024 / 1024))
                        .nodeName(nodeName)
                        .instance(instance)
                        .build());
            }
        }

        log.info("统计集群磁盘使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterDiskInfos;
    }

    @Override
    public List<ClusterDiskMemoryDetail> queryDiskUsageDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_USAGE_BYTES_DETAIL);
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<ClusterDiskMemoryDetail> clusterDiskMemoryDetails = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> {
                Map<String, String> metric = o.getMetric();
                if (null != metric) {
                    List<List<Double>> values = o.getValues();
                    ClusterDiskMemoryDetail clusterDiskMemoryDetail = new ClusterDiskMemoryDetail();
                    clusterDiskMemoryDetail.setNodeName(metricsQueryRange.getNodeName());
                    clusterDiskMemoryDetail.setInstance(metricsQueryRange.getInstance());
                    Map<Long, String> capCoreDetailMap = new HashMap<>(values.size());
                    values.forEach(value -> capCoreDetailMap.put((long) (value.get(0) * 1000), formatDouble(value.get(1) * 100) + "%"));
                    clusterDiskMemoryDetail.setDiskDetailMap(capCoreDetailMap);
                    clusterDiskMemoryDetails.add(clusterDiskMemoryDetail);
                }
            });
        }
        log.info("统计集群磁盘使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterDiskMemoryDetails;
    }

    @Override
    public List<ClusterCpuCoreInfo> queryCpuCore(String nodeName, String instance) {
        long start = System.currentTimeMillis();
        MetricsQuery metricsQuery = MetricsQuery.builder().metricsTag(PrometheusMetricsConstant.SUM_KUBE_NODE_STATUS_ALLOCATABLE_CPU).dateTime(getCurDateTime()).nodeName(nodeName).instance(instance).build();
        QueryMetricsResult allCpuCoreQueryResult = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL);
        QueryMetricsResult usageCpuCoreQueryResult = queryFromPrometheus(metricsQuery);
        List<ClusterCpuCoreInfo> clusterCpuCoreInfos = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(allCpuCoreQueryResult, usageCpuCoreQueryResult)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> allCpuCoreResult = allCpuCoreQueryResult.getData().getResult();
            List<QueryMetricsResult.DataDTO.ResultDTO> usageCpuCoreResult = usageCpuCoreQueryResult.getData().getResult();
            for (int i = 0, n = allCpuCoreResult.size(); i < n; i++) {
                double allCpuCoreSize = allCpuCoreResult.get(i).getValue().get(1);
                double usageCpuCore = usageCpuCoreResult.get(i).getValue().get(1);
                clusterCpuCoreInfos.add(ClusterCpuCoreInfo.builder()
                        .cpuCoreSize((int) allCpuCoreSize)
                        .usageCpuCore(formatDouble(usageCpuCore))
                        .nodeName(nodeName)
                        .instance(instance)
                        .build());
            }
        }

        log.info("集群节点核心使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterCpuCoreInfos;
    }

    @Override
    public List<ClusterCpuCoreDetail> queryCpuCoreDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
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
                Map<Long, String> capCoreDetailMap = new HashMap<>(values.size());
                values.forEach(value -> capCoreDetailMap.put((long) (value.get(0) * 1000), formatDouble(value.get(1)) + "%"));
                clusterCpuCoreDetail.setCpuCoreDetailMap(capCoreDetailMap);
                clusterCpuCoreDetails.add(clusterCpuCoreDetail);
            });
        }
        log.info("统计集群CPU使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterCpuCoreDetails;
    }

    @Override
    public List<ClusterDiskIoDetail> queryDiskIoDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        List<ClusterDiskIoDetail> clusterDiskIoDetailList = new ArrayList<>();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NODE_DISK_READ_TOTAL_BYTES);
        QueryRangeMetricsResult acceptQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NODE_DISK_WRITE_TOTAL_BYTES);
        QueryRangeMetricsResult sendQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        if (ObjectUtil.isAllNotEmpty(acceptQueryResult, sendQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> acceptResult = acceptQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> sendResult = sendQueryResult.getData().getResult();
            for (int i = 0, n = acceptResult.size(); i < n; i++) {
                QueryRangeMetricsResult.DataDTO.ResultDTO acceptResultDTO = acceptResult.get(i);
                QueryRangeMetricsResult.DataDTO.ResultDTO sendResultDTO = sendResult.get(i);
                Map<Long, Double> sendBytes = new HashMap<>(acceptResultDTO.getValues().size());
                Map<Long, Double> acceptBytes = new HashMap<>(sendResultDTO.getValues().size());

                // 暂定 GB 为单位
                acceptResultDTO.getValues().forEach(value -> acceptBytes.put((long) (value.get(0) * 1000), formatDouble(value.get(1) / 1024 / 1024 / 1024)));
                sendResultDTO.getValues().forEach(value -> sendBytes.put((long) (value.get(0) * 1000), formatDouble(value.get(1) / 1024 / 1024 / 1024)));

                clusterDiskIoDetailList.add(ClusterDiskIoDetail.builder().nodeName(metricsQueryRange.getNodeName()).instance(metricsQueryRange.getInstance()).receiveBytes(acceptBytes).sendBytes(sendBytes).build());
            }
        }
        log.info("查询集群节点磁盘IO情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterDiskIoDetailList;
    }

    @Override
    public List<ClusterNetworkDetail> queryNetworkInfoDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        List<ClusterNetworkDetail> clusterNetworkDetails = new ArrayList<>();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NETWORK_RECEIVE_BYTES_TOTAL);
        QueryRangeMetricsResult acceptQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NETWORK_TRANSMIT_BYTES_TOTAL);
        QueryRangeMetricsResult sendQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        if (ObjectUtil.isAllNotEmpty(acceptQueryResult, sendQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> acceptResult = acceptQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> sendResult = sendQueryResult.getData().getResult();
            for (int i = 0, n = acceptResult.size(); i < n; i++) {
                QueryRangeMetricsResult.DataDTO.ResultDTO acceptResultDTO = acceptResult.get(i);
                QueryRangeMetricsResult.DataDTO.ResultDTO sendResultDTO = sendResult.get(i);
                Map<Long, Double> sendBytes = new HashMap<>(acceptResultDTO.getValues().size());
                Map<Long, Double> acceptBytes = new HashMap<>(sendResultDTO.getValues().size());

                acceptResultDTO.getValues().forEach(value -> acceptBytes.put((long) (value.get(0) * 1000), formatDouble(value.get(1) / 1024 / 1024 / 1024)));
                sendResultDTO.getValues().forEach(value -> sendBytes.put((long) (value.get(0) * 1000), formatDouble(value.get(1) / 1024 / 1024 / 1024)));

                clusterNetworkDetails.add(ClusterNetworkDetail.builder().nodeName(metricsQueryRange.getNodeName()).instance(metricsQueryRange.getInstance()).receiveBytes(acceptBytes).sendBytes(sendBytes).build());
            }
        }
        log.info("查询集群节点网络使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterNetworkDetails;
    }

    @Override
    public List<GpuMemoryInfo> queryGpuMemoryInfo(String nodeName) {
        MetricsQuery metricsQuery = new MetricsQuery();
        metricsQuery.setDateTime(getCurDateTime());
        metricsQuery.setMetricsTag(PrometheusMetricsConstant.DCGM_GPU_USED_MEMORY);
        metricsQuery.setNodeName(nodeName);
        QueryMetricsResult usedMemory = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.DCGM_GPU_FREE_MEMORY);
        QueryMetricsResult freeMemory = queryFromPrometheus(metricsQuery);

        List<GpuMemoryInfo> clusterNodeInfoList = new ArrayList<>();

        if (ObjectUtil.isAllNotEmpty(usedMemory, freeMemory)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> usedMemoryResult = usedMemory.getData().getResult();
            List<QueryMetricsResult.DataDTO.ResultDTO> freeMemoryResult = freeMemory.getData().getResult();
            for (int i = 0; i < usedMemoryResult.size(); i++) {
                // 单位 GB
                double free = freeMemoryResult.get(i).getValue().get(1) / 1024;
                double used = usedMemoryResult.get(i).getValue().get(1) / 1024;
                clusterNodeInfoList.add(GpuMemoryInfo.builder()
                        .nodeName(nodeName)
                        .usedMemory(formatDouble(used))
                        .freeMemory(formatDouble(free))
                        .totalMemory(formatDouble(free + used))
                        .build());
            }
        }
        return clusterNodeInfoList;
    }

    @Override
    public List<GpuMemoryDetail> queryGpuMemoryDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.DCGM_GPU_USED_UTIL_RATIO);
        metricsQueryRange.setSpecific(true);
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<GpuMemoryDetail> gpuMemoryDetails = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> {
                Map<String, String> metric = o.getMetric();
                if (null != metric) {
                    String nodeName = metricsQueryRange.getNodeName();
                    List<List<Double>> values = o.getValues();
                    GpuMemoryDetail gpuMemoryDetail = new GpuMemoryDetail();
                    gpuMemoryDetail.setNodeName(nodeName);
                    Map<Long, String> capCoreDetailMap = new HashMap<>(values.size());
                    values.forEach(value -> capCoreDetailMap.put((long) (value.get(0) * 1000), formatDouble(value.get(1) * 100) + "%"));
                    gpuMemoryDetail.setMemoryUsageMap(capCoreDetailMap);
                    gpuMemoryDetails.add(gpuMemoryDetail);
                }
            });
        }
        log.info("统计集群GPU内存使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return gpuMemoryDetails;
    }

    private String getCurDateTime() {
        long timeMillis = System.currentTimeMillis();
        double timeStampWithDecimal = timeMillis / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        return decimalFormat.format(timeStampWithDecimal);
    }

    public static double formatDouble(double num) {
        BigDecimal bd = BigDecimal.valueOf(num);
        BigDecimal result = bd.setScale(2, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

}
