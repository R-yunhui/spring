package com.ral.young.boot;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot 启动类
 *
 * @author renyunhui
 * @date 2024-01-29 15:17
 * @since 1.0.0
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        /*
         * @SpringBootApplication 组合注解，包含以下注解：
         *     1.@EnableAutoConfiguration
         *     2.@ComponentScan
         *     3.@SpringBootConfiguration -> @Configuration
         */
        // 启动容器
        SpringApplication application = new SpringApplication(Application.class);
        // 设置 Banner 输出的位置
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.run();
    }
}
