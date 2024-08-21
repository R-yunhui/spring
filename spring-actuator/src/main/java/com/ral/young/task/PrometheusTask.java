package com.ral.young.task;

import cn.hutool.json.JSONObject;
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
import java.text.DecimalFormat;
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

    public static void main(String[] args) {
        String msg = "{\"ts\":\"1724141598740\",\"url\":\"/group1/certificate/20240820/08/13/1/2024-08-20-16-13-19_1823567221934608385_VideoRecording_0efe1e4a5ecc11ef90aae8136efe4cb2.mp4\",\"tags\":{\"presetId\":\"-1\",\"severity\":\"GENERAL\",\"alarmName\":\"水位检测\"},\"zoom\":0,\"boxes\":[],\"extra\":{\"itemsInBox\":[{\"confidence\":0.10000000149011612}]},\"scene\":\"/group1/alarm/20240820/08/13/1/scene-14be023d9f2a4750939b3068256faf5f.jpg?download=0\",\"score\":0.1,\"preset\":{\"id\":\"-1\",\"ptz\":\"\",\"name\":\"\",\"deviceCode\":\"51010400001311620763\",\"deviceName\":\"新疆测试\",\"channelCode\":\"51010400001310000084\",\"channelName\":\"新疆测试_0\"},\"taskId\":\"1825771319414628353\",\"channel\":{\"height\":1,\"eastPan\":0,\"orgCode\":\"1823257414592516098_1823564227871395842\",\"latitude\":0,\"northPan\":0,\"tenantId\":\"1823257414143725570\",\"longitude\":0,\"pitchAngle\":0,\"channelName\":\"新疆测试_0\",\"xCoordinate\":49.75547791,\"yCoordinate\":49.73924381,\"zCoordinate\":0,\"dueNorthAngle\":90,\"verticalFieldOfView\":0,\"horizontalFieldOfView\":0},\"eventID\":\"0efe1e4a5ecc11ef90aae8136efe4cb2\",\"cameraId\":\"1823567221934608385\",\"alarmType\":\"WaterLevel\",\"taskParam\":{\"presetId\":\"-1\",\"severity\":\"GENERAL\",\"alarmName\":\"水位检测\"},\"normalType\":false,\"sceneWidth\":1920,\"sceneHeight\":1080,\"abilityParams\":{\"id\":\"1825416036079501313\",\"eNum\":6,\"minBox\":{\"width\":10,\"height\":10},\"eMetres\":0.5,\"interval\":2,\"areaBoxes\":[],\"threshold\":0.25,\"periodTimes\":[{\"endTime\":\"23:00\",\"startTime\":\"09:00\"}],\"shieldAreas\":[],\"analysisMode\":\"VIDEO_STREAM\",\"alarmInterval\":10,\"setLineMetres\":10,\"alarmThresholdMetres\":11},\"alarmVideoStop\":\"281444919292904\",\"alarmVideoStart\":\"187659950757260\",\"alarmEventStatus\":1,\"curCollectionData\":null}";
        JSONObject object = JSONUtil.parseObj(msg);
        Float curCollectionData = object.getFloat("curCollectionData");
        if (null != curCollectionData) {
            DecimalFormat df = new DecimalFormat("#.00");
            String result = df.format(curCollectionData);
            System.out.println(result);
        }
    }

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
