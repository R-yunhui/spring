package com.ral.young.study.designpattern.behavioral.statepattern.enumstate;

import java.math.BigDecimal;

/**
 * 订单事件
 *
 * @author renyunhui
 * @date 2023-11-14 16:01
 * @since 1.0.0
 */
public class OrderEvent {

    private OrderStateEnum orderStateEnum;

    private BigDecimal bigDecimal;

    private String name;

    public OrderEvent(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
        this.orderStateEnum = OrderStateEnum.DISPATCH;
    }

    public OrderEvent nextState() {
        this.orderStateEnum = orderStateEnum.nextState();
        return this;
    }

    public void log() {
        System.out.println(this.orderStateEnum.getDesc() + " ------> " + this.name);
    }

    public OrderStateEnum getOrderStateEnum() {
        return orderStateEnum;
    }

    public void setOrderStateEnum(OrderStateEnum orderStateEnum) {
        this.orderStateEnum = orderStateEnum;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
