package com.ral.young.night.interview;

import com.ral.young.night.interview.config.InterviewConfig;
import com.ral.young.night.interview.service.TestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 *
 * @author renyunhui
 * @date 2024-06-25 9:37
 * @since 1.0.0
 */
public class InterviewApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(InterviewConfig.class);
        /*
         * 模拟解决接口幂等性的问题
         * 一锁 二判 三更新
         */
        TestService bean = applicationContext.getBean(TestService.class);
        bean.testIdempotency();
    }
}
