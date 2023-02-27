package com.ral.young.spring.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2023-02-17 10:45
 * @since 1.0.0
 */
public class AopMainStarter {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AopConfig.class);
        TestServiceOne bean = applicationContext.getBean(TestServiceOne.class);
        bean.testOne();
        applicationContext.close();
    }
}
