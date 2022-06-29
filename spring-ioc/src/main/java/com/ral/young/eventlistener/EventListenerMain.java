package com.ral.young.eventlistener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * EventListen 示例
 *
 * @author renyunhui
 * @date 2022-06-23 19:06
 * @since 1.0.0
 */
public class EventListenerMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyListenerConfig.class);
        MessageEvent messageEvent = new MessageEvent("测试消息事件");
        System.out.println("发送事件消息的线程：" + Thread.currentThread().getName());

        if (!applicationContext.containsBean("myEventListenerLazy")) {
            System.out.println("获取到的懒加载的 MyEventListenerLazy Bean 对象为空");
        }

        applicationContext.publishEvent(messageEvent);

        if (applicationContext.containsBean("myEventListenerLazy")) {
            System.out.println("获取到了懒加载的 MyEventListenerLazy Bean 对象");
        }

        applicationContext.close();
    }
}
