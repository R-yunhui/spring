package com.ral.young;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author renyunhui
 * @description 启动类
 * @date 2024-09-05 10-50-13
 * @since 1.0.0
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(value = "com.ral.young.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
