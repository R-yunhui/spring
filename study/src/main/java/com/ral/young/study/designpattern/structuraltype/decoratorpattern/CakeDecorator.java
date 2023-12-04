package com.ral.young.study.designpattern.structuraltype.decoratorpattern;

import java.math.BigDecimal;

/**
 * 蛋糕的装饰器
 *
 * @author renyunhui
 * @date 2023-12-04 14:20
 * @since 1.0.0
 */
public abstract class CakeDecorator extends Cake {

    private final Cake cake;

    public CakeDecorator(Cake cake) {
        this.cake = cake;
    }

    @Override
    public String getDes() {
        return cake.getDes();
    }

    @Override
    public BigDecimal getPrice() {
        return cake.getPrice();
    }
}
