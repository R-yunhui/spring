package com.ral.young.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ral.young.bo.*;
import com.ral.young.constant.PrometheusMetricsConstant;
import com.ral.young.enums.ResourceEnum;
import com.ral.young.service.ResourceMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
@Service(value = "resourceMonitorService")
@Slf4j
public class ResourceMonitorServiceImpl implements ResourceMonitorService {

    private static final String PROMETHEUS_URL = "http://192.168.2.20:39090";

    private static final String PROMETHEUS_QUERY_RANGE_URL = "/api/v1/query_range?query=";

    private static final String PROMETHEUS_QUERY_URL = "/api/v1/query?query=";

    private static final String NODE_NAME_TAG = "nodename";

    private static final String ALL_TAG = "all";

    private static final String INSTANCE_TAG = "instance";

    private static final String PLUS_TAG = "+";

    @Resource
    private RestTemplate restTemplate;

    @Override
    public List<NodeResourceInfo> queryNodeResourceInfo(String nodeName, String instance, String resourceEnum) {
        List<NodeResourceInfo> result = new ArrayList<>();
        switch (ResourceEnum.valueOf(resourceEnum)) {
            case CPU:
                result = queryCpuCore(nodeName, instance);
                break;
            case DISK:
                result = queryDiskUsage(nodeName, instance);
                break;
            case GPU:
                result = queryGpuMemoryInfo(nodeName, instance);
                break;
            case MEMORY:
                result = queryMemoryUsage(nodeName, instance);
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public List<NodeResourceVariationInfo> queryNodeResourceVariationInfo(MetricsQueryRange metricsQueryRange) {
        String resourceEnum = metricsQueryRange.getResourceEnum();
        List<NodeResourceVariationInfo> result = new ArrayList<>();
        switch (ResourceEnum.valueOf(resourceEnum)) {
            case CPU:
                result = queryCpuCoreDetails(metricsQueryRange);
                break;
            case DISK:
                result = queryDiskUsageDetails(metricsQueryRange);
                break;
            case GPU:
                result = queryGpuMemoryDetails(metricsQueryRange);
                break;
            case MEMORY:
                result = queryMemoryUsageDetails(metricsQueryRange);
                break;
            case DISK_IO:
                result = queryDiskIoDetails(metricsQueryRange);
                break;
            case NETWORK_IO:
                result = queryNetworkInfoDetails(metricsQueryRange);
                break;
            default:
                break;
        }
        return result;
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
                clusterNodeInfoList.add(ClusterNodeInfo.builder()
                        .nodeInstance(metric.get(INSTANCE_TAG))
                        .nodeName(metric.get(NODE_NAME_TAG))
                        .build());
            }
        }
        return clusterNodeInfoList;
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
                double allNodeSize = CollUtil.isEmpty(nodeResult.get(i).getValue()) ? 0 : nodeResult.get(i).getValue().get(1);
                double unschedulableNodeSize = CollUtil.isEmpty(unschedulableNodeResult.get(i).getValue()) ? 0 : unschedulableNodeResult.get(i).getValue().get(1);
                clusterNodeStatus = ClusterNodeStatus.builder()
                        .allNode((int) allNodeSize)
                        .failNode((int) unschedulableNodeSize)
                        .readyNode((int) allNodeSize - (int) unschedulableNodeSize)
                        .build();
            }
        }
        log.info("统计集群节点状态耗时：{} ms", System.currentTimeMillis() - start);
        return clusterNodeStatus;
    }

    public List<NodeResourceInfo> queryMemoryUsage(String nodeName, String instance) {
        long start = System.currentTimeMillis();
        MetricsQuery metricsQuery = MetricsQuery.builder().metricsTag(PrometheusMetricsConstant.SUM_NODE_TOTAL_MEMORY).dateTime(getCurDateTime()).nodeName(nodeName).instance(instance).build();
        QueryMetricsResult allMemoryQueryResult = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.SUM_NODE_FREE_MEMORY);
        List<NodeResourceInfo> result = buildNodeResource(nodeName, instance, metricsQuery, allMemoryQueryResult);
        log.info("统计集群节点内存使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return result;
    }

    public List<NodeResourceInfo> queryDiskUsage(String nodeName, String instance) {
        long start = System.currentTimeMillis();
        MetricsQuery metricsQuery = MetricsQuery.builder().metricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_LIMIT_BYTES).dateTime(getCurDateTime()).nodeName(nodeName).instance(instance).build();
        QueryMetricsResult diskStorageQueryResult = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_USAGE_BYTES);
        List<NodeResourceInfo> result = buildNodeResource(nodeName, instance, metricsQuery, diskStorageQueryResult);

        log.info("统计集群磁盘使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return result;
    }

    public List<NodeResourceInfo> queryCpuCore(String nodeName, String instance) {
        long start = System.currentTimeMillis();
        MetricsQuery metricsQuery = MetricsQuery.builder().metricsTag(PrometheusMetricsConstant.SUM_KUBE_NODE_STATUS_ALLOCATABLE_CPU).dateTime(getCurDateTime()).nodeName(nodeName).instance(instance).build();
        QueryMetricsResult allCpuCoreQueryResult = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL);
        QueryMetricsResult usageCpuCoreQueryResult = queryFromPrometheus(metricsQuery);
        List<NodeResourceInfo> clusterCpuCoreInfos = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(allCpuCoreQueryResult, usageCpuCoreQueryResult)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> allCpuCoreResult = allCpuCoreQueryResult.getData().getResult();
            List<QueryMetricsResult.DataDTO.ResultDTO> usageCpuCoreResult = usageCpuCoreQueryResult.getData().getResult();
            for (int i = 0, n = allCpuCoreResult.size(); i < n; i++) {
                double allCpuCoreSize = allCpuCoreResult.get(i).getValue().get(1);
                double usageCpuCore = usageCpuCoreResult.get(i).getValue().get(1);
                clusterCpuCoreInfos.add(NodeResourceInfo.builder()
                        .total(allCpuCoreSize)
                        .used(formatDouble(usageCpuCore))
                        .nodeName(nodeName)
                        .instance(instance)
                        .build());
            }
        }

        log.info("集群节点核心使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterCpuCoreInfos;
    }

    public List<NodeResourceInfo> queryGpuMemoryInfo(String nodeName, String instance) {
        MetricsQuery metricsQuery = new MetricsQuery();
        metricsQuery.setDateTime(getCurDateTime());
        metricsQuery.setMetricsTag(PrometheusMetricsConstant.DCGM_GPU_USED_MEMORY);
        metricsQuery.setNodeName(nodeName);
        QueryMetricsResult usedMemory = queryFromPrometheus(metricsQuery);

        metricsQuery.setMetricsTag(PrometheusMetricsConstant.DCGM_GPU_FREE_MEMORY);
        QueryMetricsResult freeMemory = queryFromPrometheus(metricsQuery);

        List<NodeResourceInfo> clusterNodeInfoList = new ArrayList<>();

        if (ObjectUtil.isAllNotEmpty(usedMemory, freeMemory)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> usedMemoryResult = usedMemory.getData().getResult();
            List<QueryMetricsResult.DataDTO.ResultDTO> freeMemoryResult = freeMemory.getData().getResult();
            for (int i = 0; i < usedMemoryResult.size(); i++) {
                // 单位 GB
                double free = freeMemoryResult.get(i).getValue().get(1) / 1024;
                double used = usedMemoryResult.get(i).getValue().get(1) / 1024;
                clusterNodeInfoList.add(NodeResourceInfo.builder()
                        .nodeName(nodeName)
                        .instance(instance)
                        .used(formatDouble(used))
                        .total(formatDouble(free + used))
                        .build());
            }
        }
        return clusterNodeInfoList;
    }

    private List<NodeResourceInfo> buildNodeResource(String nodeName, String instance, MetricsQuery metricsQuery, QueryMetricsResult allMemoryQueryResult) {
        QueryMetricsResult usageMemoryQueryResult = queryFromPrometheus(metricsQuery);
        List<NodeResourceInfo> result = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(allMemoryQueryResult, usageMemoryQueryResult)) {
            List<QueryMetricsResult.DataDTO.ResultDTO> allMemoryResult = allMemoryQueryResult.getData().getResult();
            List<QueryMetricsResult.DataDTO.ResultDTO> usageMemoryResult = usageMemoryQueryResult.getData().getResult();
            for (int i = 0, n = allMemoryResult.size(); i < n; i++) {
                double allMemorySize = allMemoryResult.get(i).getValue().get(1);
                double usageMemorySize = usageMemoryResult.get(i).getValue().get(1);
                // 单位暂定为 GB
                result.add(NodeResourceInfo.builder()
                        .total(formatDouble(allMemorySize / 1024 / 1024 / 1024))
                        .used(formatDouble(usageMemorySize / 1024 / 1024 / 1024))
                        .nodeName(nodeName)
                        .instance(instance)
                        .build());
            }
        }
        return result;
    }

    public List<NodeResourceVariationInfo> queryDiskUsageDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_CONTAINER_FS_USAGE_BYTES_DETAIL);
        List<NodeResourceVariationInfo> result = buildNodeResource(metricsQueryRange);
        log.info("统计集群磁盘使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return result;
    }

    public List<NodeResourceVariationInfo> queryCpuCoreDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_IRATE_CONTAINER_CPU_USAGE_SECONDS_TOTAL_DETAIL);
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<NodeResourceVariationInfo> clusterCpuCoreDetails = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> buildNodeResourceVariation(metricsQueryRange, clusterCpuCoreDetails, o));
        }
        log.info("统计集群CPU使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterCpuCoreDetails;
    }

    public List<NodeResourceVariationInfo> queryDiskIoDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NODE_DISK_READ_TOTAL_BYTES);
        QueryRangeMetricsResult acceptQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NODE_DISK_WRITE_TOTAL_BYTES);
        List<NodeResourceVariationInfo> clusterIoDetailList = buildNodeResourceVariation(metricsQueryRange, acceptQueryResult);
        log.info("查询集群节点磁盘IO情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterIoDetailList;
    }

    public List<NodeResourceVariationInfo> queryNetworkInfoDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NETWORK_RECEIVE_BYTES_TOTAL);
        QueryRangeMetricsResult acceptQueryResult = queryRangeFromPrometheus(metricsQueryRange);

        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NETWORK_TRANSMIT_BYTES_TOTAL);
        List<NodeResourceVariationInfo> clusterNetworkDetails = buildNodeResourceVariation(metricsQueryRange, acceptQueryResult);
        log.info("查询集群节点网络使用情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterNetworkDetails;
    }

    public List<NodeResourceVariationInfo> queryGpuMemoryDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.DCGM_GPU_USED_UTIL_RATIO);
        metricsQueryRange.setSpecific(true);

        List<NodeResourceVariationInfo> result = buildNodeResource(metricsQueryRange);
        log.info("统计集群GPU内存使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return result;
    }

    private List<NodeResourceVariationInfo> buildNodeResource(MetricsQueryRange metricsQueryRange) {
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<NodeResourceVariationInfo> result = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> {
                Map<String, String> metric = o.getMetric();
                if (null != metric) {
                    List<List<Double>> values = o.getValues();
                    NodeResourceVariationInfo gpuMemoryDetail = new NodeResourceVariationInfo();
                    gpuMemoryDetail.setNodeName(metricsQueryRange.getNodeName());
                    gpuMemoryDetail.setInstance(metricsQueryRange.getInstance());
                    List<String> timeList = new ArrayList<>();
                    List<Double> variationInfoList = new ArrayList<>();
                    values.forEach(value -> {
                        long timeStamp = (long) (value.get(0) * 1000);
                        timeList.add(DateUtil.format(DateUtil.date(timeStamp), DatePattern.NORM_DATETIME_PATTERN));
                        variationInfoList.add(formatDouble(value.get(1) * 100));
                    });
                    gpuMemoryDetail.setTimeList(timeList);
                    gpuMemoryDetail.setVariationInfoList(variationInfoList);
                    result.add(gpuMemoryDetail);
                }
            });
        }
        return result;
    }

    public List<NodeResourceVariationInfo> queryMemoryUsageDetails(MetricsQueryRange metricsQueryRange) {
        long start = System.currentTimeMillis();
        metricsQueryRange.setMetricsTag(PrometheusMetricsConstant.SUM_NODE_MEMORY_USED_RATE);
        QueryRangeMetricsResult metricsResult = queryRangeFromPrometheus(metricsQueryRange);
        List<NodeResourceVariationInfo> clusterMemoryDetails = new ArrayList<>();
        if (null != metricsResult) {
            metricsResult.getData().getResult().forEach(o -> buildNodeResourceVariation(metricsQueryRange, clusterMemoryDetails, o));
        }
        log.info("统计集群节点内存使用率情况耗时：{} ms", System.currentTimeMillis() - start);
        return clusterMemoryDetails;
    }

    private List<NodeResourceVariationInfo> buildNodeResourceVariation(MetricsQueryRange metricsQueryRange, QueryRangeMetricsResult acceptQueryResult) {
        QueryRangeMetricsResult sendQueryResult = queryRangeFromPrometheus(metricsQueryRange);
        List<NodeResourceVariationInfo> clusterIoDetailList = new ArrayList<>();
        if (ObjectUtil.isAllNotEmpty(acceptQueryResult, sendQueryResult)) {
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> acceptResult = acceptQueryResult.getData().getResult();
            List<QueryRangeMetricsResult.DataDTO.ResultDTO> sendResult = sendQueryResult.getData().getResult();
            for (int i = 0, n = acceptResult.size(); i < n; i++) {
                QueryRangeMetricsResult.DataDTO.ResultDTO acceptResultDTO = acceptResult.get(i);
                QueryRangeMetricsResult.DataDTO.ResultDTO sendResultDTO = sendResult.get(i);

                List<String> timeList = new ArrayList<>();
                List<Double> receiveBytes = new ArrayList<>();
                List<Double> sendBytes = new ArrayList<>();
                // 暂定 GB 为单位
                acceptResultDTO.getValues().forEach(value -> {
                    long timeStamp = (long) (value.get(0) * 1000);
                    timeList.add(DateUtil.format(DateUtil.date(timeStamp), DatePattern.NORM_DATETIME_PATTERN));
                    receiveBytes.add(formatDouble(value.get(1) / 1024 / 1024 / 1024));
                });
                sendResultDTO.getValues().forEach(value -> sendBytes.add(formatDouble(value.get(1) / 1024 / 1024 / 1024)));

                clusterIoDetailList.add(NodeResourceVariationInfo.builder()
                        .nodeName(metricsQueryRange.getNodeName())
                        .instance(metricsQueryRange.getInstance())
                        .timeList(timeList)
                        .receiveBytes(receiveBytes)
                        .sendBytes(sendBytes)
                        .build());
            }
        }
        return clusterIoDetailList;
    }

    private void buildNodeResourceVariation(MetricsQueryRange metricsQueryRange, List<NodeResourceVariationInfo> clusterMemoryDetails, QueryRangeMetricsResult.DataDTO.ResultDTO resultDTO) {
        List<List<Double>> values = resultDTO.getValues();
        NodeResourceVariationInfo clusterMemoryDetail = new NodeResourceVariationInfo();
        clusterMemoryDetail.setNodeName(metricsQueryRange.getNodeName());
        clusterMemoryDetail.setInstance(metricsQueryRange.getInstance());

        List<String> timeList = new ArrayList<>();
        List<Double> variationInfoList = new ArrayList<>();
        values.forEach(value -> {
            long timeStamp = (long) (value.get(0) * 1000);
            timeList.add(DateUtil.format(DateUtil.date(timeStamp), DatePattern.NORM_DATETIME_PATTERN));
            variationInfoList.add(formatDouble(value.get(1)));
        });
        clusterMemoryDetail.setTimeList(timeList);
        clusterMemoryDetail.setVariationInfoList(variationInfoList);
        clusterMemoryDetails.add(clusterMemoryDetail);
    }

    private QueryMetricsResult queryFromPrometheus(MetricsQuery metricsQuery) {
        StringBuilder urlBuilder = replaceMetricsTag(metricsQuery.getMetricsTag(), metricsQuery.getNodeName(), metricsQuery.getInstance());
        urlBuilder.append("&time=").append(metricsQuery.getDateTime());

        log.info("queryFromPrometheus 的 url：{}", urlBuilder);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(urlBuilder.toString()).build();
        ResponseEntity<QueryMetricsResult> entity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, HttpEntity.EMPTY, QueryMetricsResult.class);
        return entity.getBody();
    }

    private QueryRangeMetricsResult queryRangeFromPrometheus(MetricsQueryRange metricsQueryRange) {
        StringBuilder urlBuilder = replaceRangeMetricsTag(metricsQueryRange.getMetricsTag(), metricsQueryRange.getNodeName(), metricsQueryRange.getInstance());
        urlBuilder.append("&start=").append(metricsQueryRange.getStart());
        urlBuilder.append("&end=").append(metricsQueryRange.getEnd());
        urlBuilder.append("&step=").append(metricsQueryRange.getStep());

        log.info("queryRangeFromPrometheus 的 url：{}", urlBuilder);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(urlBuilder.toString()).build(metricsQueryRange.isSpecific());
        ResponseEntity<QueryRangeMetricsResult> entity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, HttpEntity.EMPTY, QueryRangeMetricsResult.class);
        return entity.getBody();
    }

    private StringBuilder replaceMetricsTag(String metricsTag, String nodeName, String instance) {
        if (StrUtil.isNotBlank(nodeName)) {
            if (ALL_TAG.equals(nodeName)) {
                metricsTag = metricsTag.replace(PrometheusMetricsConstant.NODE_NAME_REPLACE_TAG, PrometheusMetricsConstant.ALL_NODE_NAME);
                metricsTag = metricsTag.replace("by (instance)", StringUtils.EMPTY);
                metricsTag = metricsTag.replace("by (Hostname)", StringUtils.EMPTY);
            } else {
                metricsTag = metricsTag.replace(PrometheusMetricsConstant.NODE_NAME_REPLACE_TAG, nodeName);
            }
        }

        if (StrUtil.isNotBlank(instance)) {
            if (ALL_TAG.equals(instance)) {
                metricsTag = metricsTag.replace(PrometheusMetricsConstant.INSTANCE_NAME_REPLACE_TAG, PrometheusMetricsConstant.ALL_NODE_NAME);
                metricsTag = metricsTag.replace("by (instance)", StringUtils.EMPTY);
                metricsTag = metricsTag.replace("by (Hostname)", StringUtils.EMPTY);
            } else {
                metricsTag = metricsTag.replace(PrometheusMetricsConstant.INSTANCE_NAME_REPLACE_TAG, instance);
            }
        }

        if (metricsTag.contains(PLUS_TAG)) {
            try {
                metricsTag = URLEncoder.encode(metricsTag, "UTF-8");
            } catch (Exception e) {
                log.error("特殊字符处理异常", e);
            }
        }

        String url = PROMETHEUS_URL + ResourceMonitorServiceImpl.PROMETHEUS_QUERY_URL + metricsTag;
        return new StringBuilder(url);
    }

    private StringBuilder replaceRangeMetricsTag(String metricsTag, String nodeName, String instance) {
        if (StrUtil.isNotBlank(nodeName)) {
            if (ALL_TAG.equals(nodeName)) {
                metricsTag = metricsTag.replace(PrometheusMetricsConstant.NODE_NAME_REPLACE_TAG, PrometheusMetricsConstant.ALL_NODE_NAME);
            } else {
                metricsTag = metricsTag.replace(PrometheusMetricsConstant.NODE_NAME_REPLACE_TAG, nodeName);
            }
        }

        if (StrUtil.isNotBlank(instance)) {
            if (ALL_TAG.equals(instance)) {
                metricsTag = metricsTag.replace(PrometheusMetricsConstant.INSTANCE_NAME_REPLACE_TAG, PrometheusMetricsConstant.ALL_NODE_NAME);
            } else {
                metricsTag = metricsTag.replace(PrometheusMetricsConstant.INSTANCE_NAME_REPLACE_TAG, instance);
            }
        }

        if (metricsTag.contains(PLUS_TAG)) {
            try {
                metricsTag = URLEncoder.encode(metricsTag, "UTF-8");
            } catch (Exception e) {
                log.error("特殊字符处理异常", e);
            }
        }

        String url = PROMETHEUS_URL + ResourceMonitorServiceImpl.PROMETHEUS_QUERY_RANGE_URL + metricsTag;
        return new StringBuilder(url);
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

