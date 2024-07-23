package com.ral.young;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * 服务启动类
 *
 * @author renyunhui
 * @date 2024-07-23 15:09
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan(value = "com.ral.young.mapper")
@EnableKafka
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
