package com.ral.young.practice.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.ral.young.practice.bean.User;
import com.ral.young.practice.postprocessor.TestBeanFactoryPostProcessorOne;
import com.ral.young.practice.postprocessor.TestBeanFactoryPostProcessorTwo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.AbstractApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author renyunhui
 * @date 2022-11-16 14:06
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.practice.listener")
public class BeanConfig {

    @Bean
    public User user() {
        return new User("ryh", 24, 1);
    }

    @Bean
    public TestBeanFactoryPostProcessorOne testBeanFactoryPostProcessorOne() {
        return new TestBeanFactoryPostProcessorOne();
    }

    @Bean
    public TestBeanFactoryPostProcessorTwo testBeanFactoryPostProcessorTwo() {
        return new TestBeanFactoryPostProcessorTwo();
    }

    @Bean
    public AbstractApplicationEventMulticaster applicationEventMulticaster() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024), ThreadFactoryBuilder.create().setNamePrefix("EventListener-").build());
        // 定义一个事件多播器 - 添加异步线程池
        SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();
        simpleApplicationEventMulticaster.setTaskExecutor(threadPoolExecutor);
        return simpleApplicationEventMulticaster;
    }
}
