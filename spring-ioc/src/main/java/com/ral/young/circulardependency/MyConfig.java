package com.ral.young.circulardependency;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 测试循环依赖
 *
 * @author renyunhui
 * @date 2023-01-03 14:08
 * @since 1.0.0
 */
@Configuration
@EnableAsync
@ComponentScan(value = "com.ral.young.circulardependency")
public class MyConfig {
}
