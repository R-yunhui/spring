package com.ral.young.practice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 自定义事件监听器
 *
 * @author renyunhui
 * @date 2022-11-16 15:56
 * @since 1.0.0
 */
@Slf4j
@Component
public class MyApplicationListener implements ApplicationListener<CustomEvent> {

    @Override
    public void onApplicationEvent(@NonNull CustomEvent event) {
        log.info("监听到 CustomEvent ：{},当前执行监听任务的线程:{}", event, Thread.currentThread().getName());
    }

    @EventListener
    public void onApplicationEventTwo(@NonNull CustomEvent event) {
        log.info("监听到 CustomEvent ：{},当前执行监听任务的线程:{}", event, Thread.currentThread().getName());
    }
}
