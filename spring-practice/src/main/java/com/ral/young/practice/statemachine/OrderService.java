package com.ral.young.practice.statemachine;

import com.ral.young.practice.statemachine.enums.OrderEventEnum;
import com.ral.young.practice.statemachine.enums.OrderStatusEnum;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author renyunhui
 * @description 这是一个OrderService类
 * @date 2024-10-25 16-10-15
 * @since 1.0.0
 */
@Service
public class OrderService {

    private final StateMachine<OrderStatusEnum, OrderEventEnum> stateMachine;

    public OrderService(StateMachine<OrderStatusEnum, OrderEventEnum> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @PostConstruct
    public void init() {
        stateMachine.start();
    }

    // 处理订单支付事件
    public void processPayment(String id) {
        // 发送 PAY 事件触发状态转换
        stateMachine.sendEvent(OrderEventEnum.PAY);
    }

    // 处理订单发货事件
    public void processShipping(String id) {
        // 发送 SHIP 事件触发状态转换
        stateMachine.sendEvent(OrderEventEnum.SHIP);
    }

    // 处理订单完成事件
    public void processCompletion(String id) {
        // 发送 COMPLETE 事件触发状态转换
        stateMachine.sendEvent(OrderEventEnum.COMPLETE);
    }
}
