package com.ral.young.spring.event;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2023-02-16 16:21
 * @since 1.0.0
 */
public class SpringEventMainStarter {

    public static void main(String[] args) {
        // AnnotationConfigApplicationContext 实现了 ApplicationEventPublisher，也是一个事件发布器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringEventConfig.class);

        DataChangedEvent dataChangedEvent = new DataChangedEvent("", "数据发生了改变");
        applicationContext.publishEvent(dataChangedEvent);

        applicationContext.close();
    }
}
