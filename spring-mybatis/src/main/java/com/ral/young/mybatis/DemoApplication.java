package com.ral.young.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author renyunhui
 * @date 2023-05-24 13:46
 * @since 1.0.0
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(value = "com.ral.young.mybatis.dao")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class);
    }
}
