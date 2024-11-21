package com.ral.young.spring.basic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author renyunhui
 * @description 这是一个SchedulingConfig类
 * @date 2024-11-21 16-23-13
 * @since 1.0.0
 */
@Configuration
// @EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Value("${spring.task.scheduling.pool.size:10}")
    private int poolSize;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 设置线程池大小为10
        scheduler.setPoolSize(poolSize);
        // 设置线程名前缀，方便排查问题
        scheduler.setThreadNamePrefix("scheduled-task-");
        // 设置等待终止的时间，即关闭线程池时最多等待的时间
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }
}
