package com.ral.young.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author renyunhui
 * @description 这是一个NewApplication类
 * @date 2025-01-07 14-24-05
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class NewApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewApplication.class, args);
    }
}
