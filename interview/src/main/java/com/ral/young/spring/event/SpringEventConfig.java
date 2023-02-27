package com.ral.young.spring.event;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author renyunhui
 * @date 2023-02-16 16:22
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.spring.event")
public class SpringEventConfig {

    ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1024), ThreadFactoryBuilder.create().setNamePrefix("applicationEventMulticaster-").build());

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        // 注册一个自定义的 ApplicationEventMulticaster，事件多播器，广播事件
        SimpleApplicationEventMulticaster applicationEventMulticaster = new SimpleApplicationEventMulticaster();
        // 自定义处理事件的线程池
        applicationEventMulticaster.setTaskExecutor(executor);
        // 自定义处理事件的异常处理器
        applicationEventMulticaster.setErrorHandler(new ApplicationEventMulticasterErrorHandler());
        return applicationEventMulticaster;
    }


}
