package com.ral.young.boot.config;

import com.ral.young.boot.component.TestServiceTwo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author renyunhui
 * @date 2024-01-29 17:04
 * @since 1.0.0
 */
@Configuration
public class ApplicationConfig implements SchedulingConfigurer {

    @Bean
    public TestServiceTwo testServiceTwo() {
        TestServiceTwo testServiceTwo = new TestServiceTwo();
        testServiceTwo.setName("SpringTestServiceTwo");
        return testServiceTwo;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix("push-alarm-scheduled-task-");
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
