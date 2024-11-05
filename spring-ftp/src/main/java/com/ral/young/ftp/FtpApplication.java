package com.ral.young.ftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author renyunhui
 * @description 这是一个FtpApplication类
 * @date 2024-10-30 09-26-38
 * @since 1.0.0
 */
@SpringBootApplication
public class FtpApplication {

    public static void main(String[] args) {
        SpringApplication.run(FtpApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
