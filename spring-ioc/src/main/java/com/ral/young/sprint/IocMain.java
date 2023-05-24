package com.ral.young.sprint;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author renyunhui
 * @date 2023-05-23 11:12
 * @since 1.0.0
 */
public class IocMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(IocFullModConfig.class, IocLiteModConfig.class);

        applicationContext.refresh();

        Animal animal = applicationContext.getBean(Animal.class);
        Car car = applicationContext.getBean(Car.class);
        System.out.println(animal.getCar().equals(car));

        User user = applicationContext.getBean(User.class);
        Tank tank = applicationContext.getBean(Tank.class);
        System.out.println(user.getTank().equals(tank));

        applicationContext.close();
    }
}
