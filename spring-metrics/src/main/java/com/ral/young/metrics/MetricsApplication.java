package com.ral.young.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author renyunhui
 * @description 这是一个MetricsApplication类
 * @date 2024-12-02 15-52-44
 * @since 1.0.0
 */
@SpringBootApplication
// @EnableScheduling
public class MetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricsApplication.class, args);
    }
}
