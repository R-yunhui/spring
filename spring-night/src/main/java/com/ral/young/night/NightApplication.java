package com.ral.young.night;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author renyunhui
 * @date 2024-06-26 14:28
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.ral.young.night.project"})
@MapperScan(value = {"com.ral.young.night.project.mapper"})
@EnableScheduling
@EnableAsync
@EnableKafka
public class NightApplication {

    public static void main(String[] args) {
        SpringApplication.run(NightApplication.class, args);

        // 注册一个 kill 15 的回调
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // kill 15 的方式会回调这个 Hook
            System.out.println("shutdown hook execute");
        }));
    }
}
