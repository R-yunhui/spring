package com.ral.young.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * IOC 配置类
 *
 * @author renyunhui
 * @date 2022-06-20 15:58
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.ioc.test")
public class IocConfig {

    @Bean
    public Car car() {
        return new Car(1, "劳斯莱斯");
    }
}
