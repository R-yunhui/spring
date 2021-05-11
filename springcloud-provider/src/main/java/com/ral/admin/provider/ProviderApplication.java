package com.ral.admin.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-12 17:19
 * @Describe:
 * @Modify:
 */
@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
