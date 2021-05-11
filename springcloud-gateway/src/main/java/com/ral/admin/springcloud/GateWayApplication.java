package com.ral.admin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-08 13:29
 * @Describe:
 * @Modify:
 */
@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
public class GateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class, args);
    }
}
