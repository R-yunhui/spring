package com.ral.young.ioc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author renyunhui
 * @date 2022-06-20 14:21
 * @since 1.0.0
 */
@Slf4j
public class IocMain {

    public static void main(String[] args) {
        // new 一个 ApplicationContext 应用程序上下文
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(IocConfig.class);

        Car car = applicationContext.getBean(Car.class);
        log.info("获取的 car 信息 : {}", car);
    }
}
