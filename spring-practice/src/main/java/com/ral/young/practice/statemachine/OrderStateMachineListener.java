package com.ral.young.practice.statemachine;

import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

/**
 * @author renyunhui
 * @description 这是一个OrderStateMachineListener类
 * @date 2024-10-25 16-11-17
 * @since 1.0.0
 */
@Component
@WithStateMachine
// 监听特定状态，并进行相关的业务处理
public class OrderStateMachineListener {

    @OnTransition(target = "CREATED")
    public void onOrderCreated() {
        System.out.println("订单已创建...");
        // 在订单创建时，可以执行一些后续动作，例如初始化订单状态等
    }

    @OnTransition(target = "PAID")
    public void onPaymentProcessed() {
        System.out.println("订单支付已处理...");
        // 在订单支付完成时，可以执行一些后续动作，例如更新订单状态、发送通知等
    }

    @OnTransition(target = "SHIPPED")
    public void onOrderShipped() {
        System.out.println("订单已发货...");
        // 在订单发货完成时，可以执行一些后续动作，例如更新订单状态、发送通知等
    }

    @OnTransition(target = "COMPLETED")
    public void onOrderCompleted() {
        System.out.println("订单已完成...");
        // 在订单完成时，可以执行一些后续动作，例如更新订单状态、发送通知等
    }
}

