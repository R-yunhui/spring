package com.ral.young.practice;

import com.ral.young.practice.config.IocConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author renyunhui
 * @date 2023-04-12 10:20
 * @since 1.0.0
 */
@Slf4j
public class MainClass {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(IocConfig.class);

        User bean = applicationContext.getBean(User.class);
        log.info("user is exists:{}", bean);
    }
}
