package com.ral.young.spring.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author renyunhui
 * @description 这是一个DubboApplication类
 * @date 2024-11-28 11-46-39
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDubbo
public class DubboApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboApplication.class, args);
    }
}
