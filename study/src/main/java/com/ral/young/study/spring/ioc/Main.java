package com.ral.young.study.spring.ioc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2023-11-20 14:33
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.registerBean("myBeanFactoryPostProcessorTwo", MyBeanFactoryPostProcessorTwo.class);
        applicationContext.registerBean("myBeanFactoryPostProcessor", MyBeanFactoryPostProcessor.class, o -> {
            // 通过这个函数式接口可以回调 BeanDefinition
            System.out.println(o.getBeanClassName());
            System.out.println(o.getDescription());
        });

        applicationContext.refresh();

        Object car = applicationContext.getBean("car");
        System.out.println(car);
    }
}
