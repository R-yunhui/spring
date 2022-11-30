package com.ral.young.practice.bean;

/**
 *
 * @author renyunhui
 * @date 2022-11-17 10:36
 * @since 1.0.0
 */
public class Order {

    private String orderName;

    private int orderId;

    private User user;

    public Order() {
    }

    public Order(String orderName, int orderId) {
        this.orderName = orderName;
        this.orderId = orderId;
    }

    public Order(String orderName, int orderId, User user) {
        this.orderName = orderName;
        this.orderId = orderId;
        this.user = user;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderName='" + orderName + '\'' +
                ", orderId=" + orderId +
                '}';
    }
}
