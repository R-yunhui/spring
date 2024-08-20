package com.ral.young.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renyunhui
 * @description 接收 Prometheus 的告警信息
 * @date 2024-08-15 16-59-32
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/hook")
@Slf4j
public class AlertController {

    public static final Map<String, Object> ALERT_FAIL = new HashMap<>();

    public static final Map<String, Object> ALERT_SUCCESS = new HashMap<>();

    static {
        ALERT_FAIL.put("msg", "报警失败");
        ALERT_FAIL.put("code", 0);
        ALERT_SUCCESS.put("msg", "报警成功");
        ALERT_SUCCESS.put("code", 1);
    }


    @RequestMapping(value = "/alert", produces = "application/json;charset=UTF-8")
    public String alert(@RequestBody String json) {
        // grafana 的 webhook
        // {"title":"[Alerting] Panel Title alert","ruleId":1,"ruleName":"Panel Title alert","state":"alerting","evalMatches":[{"value":80,"metric":"method_count_total{application=\"spring-boot-actuator\", instance=\"localhost:20081\", job=\"spring-boot-actuator\", methodName=\"com.ral.young.controller.MethodCountController.deleteCount\"}","tags":{"__name__":"method_count_total","application":"spring-boot-actuator","instance":"localhost:20081","job":"spring-boot-actuator","methodName":"com.ral.young.controller.MethodCountController.deleteCount"}}],"orgId":1,"dashboardId":1,"panelId":43,"tags":{},"ruleUrl":"http://localhost:3000/d/UDdpyzz7z/prometheus-2-0-stats?tab=alert\u0026viewPanel=43\u0026orgId=1"}
        log.debug("alert notify  params: {}", json);
        return json;
    }

}
