package com.ral.young.practice.config;

import com.ral.young.practice.Car;
import com.ral.young.practice.Tank;
import com.ral.young.practice.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author renyunhui
 * @date 2023-04-12 10:21
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.practice")
public class IocConfig {

    @Bean
    @ConditionalOnMissingBean(value = Car.class)
    public User user() {
        return new User();
    }

    @Bean
    public Tank tank() {
        return new Tank();
    }
}
