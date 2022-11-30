package com.ral.young.practice.config;

import com.ral.young.practice.bean.Order;
import com.ral.young.practice.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 验证 {@link org.springframework.context.annotation.ConfigurationClassPostProcessor}
 *
 * @author renyunhui
 * @date 2022-11-17 10:34
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class TestConfig {

    @Bean
    public User user() {
        log.info("注入 user");
        return new User();
    }

    @Bean
    public Order order() {
        log.info("注入 order");
        // 不添加 @Configuration 注解会执行两次 user 的注入，但是添加了 @Configuration 注解之后不会，会生成一个 cglib 的动态代理，后续直接从容器中获取对象
        User user = user();
        return new Order("test", 1, user);
    }
}
