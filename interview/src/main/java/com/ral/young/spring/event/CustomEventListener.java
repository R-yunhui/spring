package com.ral.young.spring.event;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 自定义事件监听器
 * {@link org.springframework.context.ApplicationListener}
 *
 * @author renyunhui
 * @date 2023-02-16 16:28
 * @since 1.0.0
 */
@Component
@Slf4j
public class CustomEventListener implements ApplicationListener<DataChangedEvent> {
    @Override
    public void onApplicationEvent(@NotNull DataChangedEvent event) {
        log.info("接口线程:{},监听到发生了 DataChangedEvent,消息:{},开始处理事件", Thread.currentThread().getName(), event.getMsg());
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("接口线程:{},处理事件完成", Thread.currentThread().getName());
    }

    @EventListener
    public void dealApplicationEvent(@NotNull DataChangedEvent event) {
        log.info("注解线程:{},监听到发生了 DataChangedEvent,消息:{},开始处理事件", Thread.currentThread().getName(), event.getMsg());
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("注解线程:{},处理事件完成", Thread.currentThread().getName());
    }
}
