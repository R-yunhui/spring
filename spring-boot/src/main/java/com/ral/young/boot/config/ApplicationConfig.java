package com.ral.young.boot.config;

import com.ral.young.boot.component.TestServiceTwo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author renyunhui
 * @date 2024-01-29 17:04
 * @since 1.0.0
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public TestServiceTwo testServiceTwo() {
        TestServiceTwo testServiceTwo = new TestServiceTwo();
        testServiceTwo.setName("SpringTestServiceTwo");
        return testServiceTwo;
    }
}
