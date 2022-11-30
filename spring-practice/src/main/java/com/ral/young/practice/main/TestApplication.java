package com.ral.young.practice.main;

import com.ral.young.practice.config.TestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2022-11-17 10:35
 * @since 1.0.0
 */
public class TestApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestConfig.class);

        applicationContext.close();
    }
}
