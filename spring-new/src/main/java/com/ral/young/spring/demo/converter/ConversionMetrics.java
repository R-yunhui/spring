package com.ral.young.spring.demo.converter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConversionMetrics {

    public static void recordMetrics(String type, boolean success, long durationMs) {
        log.info("Format conversion: type={}, success={}, duration={}ms",
                type, success, durationMs);
        // 这里可以添加实际的指标收集逻辑，比如Prometheus
    }
} 