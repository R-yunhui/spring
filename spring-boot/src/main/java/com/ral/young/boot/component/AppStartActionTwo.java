package com.ral.young.boot.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用程序启动后执行操作一
 * 通过 {@link org.springframework.boot.ApplicationRunner}
 *
 * @author renyunhui
 * @date 2024-01-29 16:46
 * @since 1.0.0
 */
@Component
@Slf4j
public class AppStartActionTwo implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("应用程序启动后执行操作二，使用方式：{}，参数：{}", "org.springframework.boot.ApplicationRunner", args);
    }
}
