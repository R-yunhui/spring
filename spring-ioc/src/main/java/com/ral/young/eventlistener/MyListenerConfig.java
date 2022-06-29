package com.ral.young.eventlistener;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 监听器配置类
 *
 * @author renyunhui
 * @date 2022-06-29 17:08
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.eventlistener")
public class MyListenerConfig {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), ThreadFactoryBuilder.create().setNamePrefix("test-").build());

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();
        simpleApplicationEventMulticaster.setTaskExecutor(EXECUTOR);
        return simpleApplicationEventMulticaster;
    }

    @Bean(value = "myEventListenerLazy")
    @Lazy
    public MyEventListenerLazy applicationListener() {
        return new MyEventListenerLazy();
    }
}
