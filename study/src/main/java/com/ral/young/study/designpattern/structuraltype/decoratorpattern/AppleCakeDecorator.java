package com.ral.young.study.designpattern.structuraltype.decoratorpattern;

import java.math.BigDecimal;

/**
 * 苹果蛋糕装饰器
 *
 * @author renyunhui
 * @date 2023-12-04 14:22
 * @since 1.0.0
 */
public class AppleCakeDecorator extends CakeDecorator {

    public AppleCakeDecorator(Cake cake) {
        super(cake);
    }

    @Override
    public String getDes() {
        return super.getDes() + " + 一个苹果";
    }

    @Override
    public BigDecimal getPrice() {
        return super.getPrice().add(new BigDecimal(10));
    }
}
