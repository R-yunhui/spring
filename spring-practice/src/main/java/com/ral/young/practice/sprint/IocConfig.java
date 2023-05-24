package com.ral.young.practice.sprint;

import com.ral.young.practice.sprint.bean.Car;
import com.ral.young.practice.sprint.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author renyunhui
 * @date 2023-05-24 10:18
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.practice.sprint")
public class IocConfig {

    @Bean(initMethod = "customInit", destroyMethod = "customDestroy", name = "user")
    public User user() {
        return new User(1L, "Bob", 21);
    }

    @Bean
    public Car car() {
        return new Car(1L, "劳斯莱斯");
    }
}
