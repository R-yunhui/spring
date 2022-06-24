package com.ral.young.eventlistener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author renyunhui
 * @date 2022-06-23 19:10
 * @since 1.0.0
 */
@Component
public class MyEventListener implements ApplicationListener<MessageEvent> {

    @Override
    public void onApplicationEvent(MessageEvent event) {
        System.out.println("接受事件消息的线程：" + Thread.currentThread().getName());
        System.out.println("监听到事件消息:" + event.getSource());
    }
}
