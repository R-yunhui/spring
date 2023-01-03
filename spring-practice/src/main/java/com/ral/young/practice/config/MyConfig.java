package com.ral.young.practice.config;

import com.ral.young.practice.bean.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author renyunhui
 * @date 2022-11-30 18:50
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.practice.bean")
public class MyConfig {

    @Bean
    public Student student() {
        return new Student("TOM", 11);
    }
}
