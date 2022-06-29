package com.ral.young.eventlistener;

import org.springframework.context.ApplicationListener;

/**
 * 自定义懒加载的事件监听器
 *
 * @author renyunhui
 * @date 2022-06-29 17:24
 * @since 1.0.0
 */
public class MyEventListenerLazy implements ApplicationListener<MessageEvent> {

    @Override
    public void onApplicationEvent(MessageEvent event) {
        System.out.println("懒加载事件监听器接受事件消息的线程：" + Thread.currentThread().getName());
        System.out.println("懒加载事件监听器监听到事件消息:" + event.getSource());
    }
}
