package com.ral.young.spring.basic;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author renyunhui
 * @description 这是一个BasicApplication类
 * @date 2024-11-20 15-15-17
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.ral.young.spring.basic.mapper")
@EnableTransactionManagement
@EnableDubbo
public class BasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicApplication.class, args);
    }
}
