package com.ral.young.practice.retry;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author renyunhui
 * @description 这是一个RetryApplication类
 * @date 2024-10-10 10-27-09
 * @since 1.0.0
 */
public class RetryApplication {

    public static void main(String[] args) throws Throwable {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TestConfig.class);
        context.refresh();

        TestService bean = context.getBean(TestService.class);
        // bean.testDo(1);

        bean.testDoTwo(2);
        context.close();
    }
}
