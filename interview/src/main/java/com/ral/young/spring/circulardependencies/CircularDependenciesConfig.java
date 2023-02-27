package com.ral.young.spring.circulardependencies;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 *
 * @author renyunhui
 * @date 2023-02-16 19:44
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.spring.circulardependencies")
@EnableAsync
public class CircularDependenciesConfig {
}
