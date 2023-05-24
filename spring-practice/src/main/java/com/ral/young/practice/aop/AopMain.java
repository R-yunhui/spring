package com.ral.young.practice.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2023-05-24 10:58
 * @since 1.0.0
 */
public class AopMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AopConfig.class);

        TestOne bean = applicationContext.getBean(TestOne.class);
        bean.test();

        applicationContext.close();
    }
}
