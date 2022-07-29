package com.ral.young.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2022-07-29 16:07
 * @since 1.0.0
 */
public class MainStart {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);

        Car car = applicationContext.getBean(Car.class);
        System.out.println(car.getTank().getId());
        System.out.println(car);
    }
}
