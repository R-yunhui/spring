package com.ral.young.study.designpattern.behavioral.policypattern.statepattern.enumstate;

import java.math.BigDecimal;

/**
 *
 * @author renyunhui
 * @date 2023-11-14 16:05
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal(10);
        OrderEvent orderEvent = new OrderEvent(bigDecimal);
        orderEvent.setName("电脑");
        orderEvent.log();

        orderEvent.nextState();
        orderEvent.log();

        orderEvent.nextState();
        orderEvent.log();
    }
}
