package com.ral.young.night.spring.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

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
public class ScheduledConfig implements SchedulingConfigurer {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduled-task-");
        // 优雅停机
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 等待终止时间
        threadPoolTaskScheduler.setAwaitTerminationMillis(60);
        return threadPoolTaskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler());
    }
}
