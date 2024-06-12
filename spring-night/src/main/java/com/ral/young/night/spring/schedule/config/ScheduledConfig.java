package com.ral.young.night.spring.schedule.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 开启定时任务
 *
 * @author renyunhui
 * @date 2024-06-12 13:55
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
@ComponentScan(value = "com.ral.young.night.spring.schedule")
public class ScheduledConfig {

}
