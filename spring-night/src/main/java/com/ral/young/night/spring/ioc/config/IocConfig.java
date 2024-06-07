package com.ral.young.night.spring.ioc.config;

import cn.hutool.core.util.IdUtil;
import com.ral.young.night.spring.ioc.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * IOC 配置类
 *
 * @author renyunhui
 * @date 2024-06-07 15:02
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.night.spring.ioc.beanpostprocessor")
public class IocConfig {

    @Bean(initMethod = "customInitMethod", destroyMethod = "customDestroy")
    public User user() {
        User user = new User();
        user.setId(IdUtil.getSnowflakeNextId());
        user.setName("Mike");
        return user;
    }
}
