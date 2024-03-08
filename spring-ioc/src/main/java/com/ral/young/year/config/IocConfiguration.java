package com.ral.young.year.config;

import com.ral.young.year.service.TestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author renyunhui
 * @date 2024-03-07 10:07
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.year")
public class IocConfiguration {

    @Bean
    public TestService  testService() {
        return new TestService();
    }
}
