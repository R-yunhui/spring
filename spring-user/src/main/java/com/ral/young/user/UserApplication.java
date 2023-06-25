package com.ral.young.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户模块启动类
 *
 * @author renyunhui
 * @date 2023-06-20 14:29
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan(value = "com.ral.young.user.dao")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
