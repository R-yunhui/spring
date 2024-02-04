package com.ral.young.boot.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 应用程序启动后执行操作一
 * 通过 {@link org.springframework.boot.CommandLineRunner}
 *
 * @author renyunhui
 * @date 2024-01-29 16:46
 * @since 1.0.0
 */
@Component
@Slf4j
public class AppStartActionOne implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("应用程序启动后执行操作一，使用方式：{}，参数：{}", "org.springframework.boot.CommandLineRunner", args);
    }
}
