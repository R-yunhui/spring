package com.ral.young.practice.statemachine.config;

import com.ral.young.practice.statemachine.enums.OrderEventEnum;
import com.ral.young.practice.statemachine.enums.OrderStatusEnum;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * @author renyunhui
 * @description 这是一个OrderStateMachineConfig类
 * @date 2024-10-25 16-07-34
 * @since 1.0.0
 */
@EnableStateMachine
@Configuration
@ComponentScan(value = "com.ral.young.practice.statemachine")
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderEventEnum> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(OrderStatusEnum.CREATED) // 设置初始状态为 CREATED
                .states(EnumSet.allOf(OrderStatusEnum.class)); // 添加所有可能的状态
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderEventEnum> transitions) throws Exception {
        transitions
                .withExternal() // 定义外部状态转换
                .source(OrderStatusEnum.CREATED).target(OrderStatusEnum.PAID).event(OrderEventEnum.PAY) // 从 CREATED 状态转换到 PAID 状态，当触发 PAY 事件时
                .and() // 连接下一个状态转换
                .withExternal() // 定义外部状态转换
                .source(OrderStatusEnum.PAID).target(OrderStatusEnum.SHIPPED).event(OrderEventEnum.SHIP) // 从 PAID 状态转换到 SHIPPED 状态，当触发 SHIP 事件时
                .and() // 连接下一个状态转换
                .withExternal() // 定义外部状态转换
                .source(OrderStatusEnum.SHIPPED).target(OrderStatusEnum.COMPLETED).event(OrderEventEnum.COMPLETE); // 从 SHIPPED 状态转换到 COMPLETED 状态，当触发 COMPLETE 事件时
    }
}
