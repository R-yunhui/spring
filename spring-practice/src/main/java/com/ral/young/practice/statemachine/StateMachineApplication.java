package com.ral.young.practice.statemachine;

import com.ral.young.practice.statemachine.config.OrderStateMachineConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author renyunhui
 * @description 这是一个StateMachineApplication类
 * @date 2024-10-25 16-04-57
 * @since 1.0.0
 */
public class StateMachineApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(OrderStateMachineConfig.class);
        applicationContext.refresh();

        OrderService service = applicationContext.getBean(OrderService.class);
        service.processPayment("111");

        service.processShipping("111");

        service.processCompletion("111");

        applicationContext.close();
    }
}
