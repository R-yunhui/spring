package ioc.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author renyunhui
 * @date 2022-06-20 14:21
 * @since 1.0.0
 */
public class IocMain {

    public static void main(String[] args) {
        // new 一个 ApplicationContext 应用程序上下文
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.refresh();

        Car car = applicationContext.getBean(Car.class);
        System.out.println(car);
    }
}
