package com.ral.young.aop;

import com.ral.young.aop.service.ITestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author renyunhui
 * @date 2022-07-01 16:20
 * @since 1.0.0
 */
public class AopMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AopConfig.class);

        ITestService bean = (ITestService) applicationContext.getBean("testService");
        bean.testLog();
    }
}
