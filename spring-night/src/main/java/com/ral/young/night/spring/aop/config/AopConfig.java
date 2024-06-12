package com.ral.young.night.spring.aop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 *
 * @author renyunhui
 * @date 2024-06-12 16:33
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.night.spring.aop")
@EnableAsync
public class AopConfig {
}
