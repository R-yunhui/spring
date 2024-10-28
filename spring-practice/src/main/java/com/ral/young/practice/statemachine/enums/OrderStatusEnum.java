package com.ral.young.practice.statemachine.enums;

/**
 * @author renyunhui
 * @description 这是一个OrderStateEnum类
 * @date 2024-10-25 16-05-29
 * @since 1.0.0
 */
public enum OrderStatusEnum {

    /**
     * 订单创建
     */
    CREATED,

    /**
     * 订单支付
     */
    PAID,

    /**
     * 订单发货
     */
    SHIPPED,

    /**
     * 订单完成
     */
    COMPLETED
}
