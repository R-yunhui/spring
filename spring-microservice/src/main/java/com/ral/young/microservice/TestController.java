package com.ral.young.microservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author renyunhui
 * @date 2022-08-09 14:04
 * @since 1.0.0
 */
@RestController
@RefreshScope
public class TestController {

    @Value("${user.name}")
    private String username;

    @Value("${user.age}")
    private int age;

    @GetMapping(value = "/test")
    public void test() {
        System.err.println("用户名:" + username + "  用户年龄:" + age);
    }
}
