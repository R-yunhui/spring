package com.ral.young.year;

import com.ral.young.year.bean.Car;
import com.ral.young.year.config.IocConfiguration;
import com.ral.young.year.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * ioc 启动
 *
 * @author renyunhui
 * @date 2024-03-07 10:06
 * @since 1.0.0
 */
@Slf4j
public class IocApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(IocConfiguration.class);

        Car bean = applicationContext.getBean(Car.class);
        log.info("bean:{}", bean);

        TestService testService = applicationContext.getBean(TestService.class);
        testService.test();

        applicationContext.close();
    }
}
