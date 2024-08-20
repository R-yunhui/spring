package com.ral.young.task;

import cn.hutool.json.JSONUtil;
import com.ral.young.bo.MetricsQuery;
import com.ral.young.bo.QueryMetricsResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author renyunhui
 * @description 这是一个PrometheusTask类
 * @date 2024-08-20 10-42-40
 * @since 1.0.0
 */
@Component
@Slf4j
public class PrometheusTask {

    @Value("${prometheus.address}")
    private String prometheusUrl;

    @Resource
    private RestTemplate restTemplate;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    /**
     * 学习中心从 Prometheus 获取 gpu 指标 key
     */
    public static final String REDIS_LEARN_PROMETHEUS_METRICS = "learn:prometheus:gpu:metrics:";

    private static final String QUERY_METRICS_URL = "/api/v1/query?query=";

    @Scheduled(fixedRateString = "${scheduled.fixedRateTime}", initialDelayString = "${scheduled.initialDelayTimeOne}")
    public void queryGpuTotalSize() {
        String metricsTag = "nvidia_smi_memory_total_bytes";
        queryFromPrometheus(metricsTag);
    }

    @Scheduled(fixedRateString = "${scheduled.fixedRateTime}", initialDelayString = "${scheduled.initialDelayTimeTwo}")
    public void queryGpuUsedBytes() {
        String metricsTag = "nvidia_smi_memory_used_bytes";
        queryFromPrometheus(metricsTag);
    }

    @Scheduled(fixedRateString = "${scheduled.fixedRateTime}", initialDelayString = "${scheduled.initialDelayTimeThree}")
    public void queryGpuIndex() {
        String metricsTag = "nvidia_smi_index";
        queryFromPrometheus(metricsTag);
    }

    private void queryFromPrometheus(String metricsTag) {
        MetricsQuery metricsQuery = buildMetricsQuery(metricsTag);
        String url = prometheusUrl + QUERY_METRICS_URL + metricsTag;
        StringBuilder builder = new StringBuilder(url);
        buildQueryTag(metricsQuery.getLabelMap(), builder);
        log.info("本次查询 prometheus 的 url：{}", builder);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(builder.toString()).build();
        ResponseEntity<QueryMetricsResult> entity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, HttpEntity.EMPTY, QueryMetricsResult.class);
        QueryMetricsResult body = entity.getBody();
        if (HttpStatus.OK.value() == entity.getStatusCodeValue() && null != body) {
            String redisKey = REDIS_LEARN_PROMETHEUS_METRICS + metricsTag;
            redisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(body.getData()));
        }
    }

    private static void buildQueryTag(Map<String, String> labelMap, StringBuilder builder) {
        String labelBuilder = "";
        if (MapUtils.isNotEmpty(labelMap)) {
            builder.append("{");
            labelMap.forEach((k, v) -> builder.append(k).append("=").append("'").append(v).append("'").append(","));
            builder.deleteCharAt(builder.length() - 1);
            builder.append("}");
        }

        builder.append(labelBuilder);
    }

    private static MetricsQuery buildMetricsQuery(String metricsTag) {
        Map<String, String> labelMap = new HashMap<>(8);
        labelMap.put("gpu", "windows");
        labelMap.put("instance", "127.0.0.1:9835");
        MetricsQuery metricsQuery = new MetricsQuery();
        metricsQuery.setDateTime(null);
        metricsQuery.setLabelMap(labelMap);

        metricsQuery.setMetricsTag(metricsTag);
        return metricsQuery;
    }
}
