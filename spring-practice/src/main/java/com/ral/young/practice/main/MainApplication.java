package com.ral.young.practice.main;

import com.ral.young.practice.bean.User;
import com.ral.young.practice.config.BeanConfig;
import com.ral.young.practice.listener.CustomEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2022-11-16 14:02
 * @since 1.0.0
 */
public class MainApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanConfig.class);

        applicationContext.refresh();

        User bean = applicationContext.getBean(User.class);
        System.out.println(bean);
        // 发布一个事件
        CustomEvent customEvent = new CustomEvent(bean);
        applicationContext.publishEvent(customEvent);

        applicationContext.close();
    }
}
