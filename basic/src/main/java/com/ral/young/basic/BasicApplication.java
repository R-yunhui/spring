package com.ral.young.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author renyunhui
 * @date 2023-06-02 10:04
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.ral.young.basic.spring"})
public class BasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicApplication.class, args);
    }
}
