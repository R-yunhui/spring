package com.ral.young.night.spring.schedule;

import com.ral.young.night.spring.schedule.config.ScheduledConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Spring 定时
 *
 * @author renyunhui
 * @date 2024-06-12 13:49
 * @since 1.0.0
 */
public class ScheduledApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ScheduledConfig.class);

        /*
         * @EnableScheduling 开启定时任务
         * 通过 @Import 引入配置类 - SchedulingConfiguration
         * 配置类注入了：ScheduledAnnotationBeanPostProcessor bean 的后置处理器
         *
         * 通过 ScheduledAnnotationBeanPostProcessor Bean 的后置处理器进行注解的解析和定时任务的执行
         * 本质是调用 ScheduledExecutorService 的 api 进行定时的调度
         *
         * 1.实现 SchedulingConfigurer 设置 setTaskScheduler 线程池，注入一个 bean：ThreadPoolTaskScheduler 修改默认的线程池的配置（优先级高）
         * 2.实现 ScheduledExecutorService 获取线程池，注入一个 bean：CustomScheduledExecutorService，实现自定义线程池
         *
         * 依赖：ScheduledTaskRegistrar  -  org.springframework.scheduling.config.ScheduledTaskRegistrar.setScheduler
         *
         * 具体执行的逻辑：org.springframework.scheduling.config.ScheduledTaskRegistrar.scheduleTasks
         *
         * 【注】：没有设置线程池：默认是 Executors.newSingleThreadScheduledExecutor();  最新线程数 Integer.MAX_VALUE
         *
         */

        applicationContext.refresh();
    }
}
