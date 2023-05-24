package com.ral.young.practice.ioc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2023-05-23 15:00
 * @since 1.0.0
 */
public class IocMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(IocConfig.class);

        applicationContext.close();
    }
}
