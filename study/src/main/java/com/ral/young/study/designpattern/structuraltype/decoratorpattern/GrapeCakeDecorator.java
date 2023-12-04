package com.ral.young.study.designpattern.structuraltype.decoratorpattern;

import java.math.BigDecimal;

/**
 * 葡萄蛋糕装饰器
 *
 * @author renyunhui
 * @date 2023-12-04 14:23
 * @since 1.0.0
 */
public class GrapeCakeDecorator extends CakeDecorator {

    public GrapeCakeDecorator(Cake cake) {
        super(cake);
    }

    @Override
    public String getDes() {
        return super.getDes() + " + 一串葡萄";
    }

    @Override
    public BigDecimal getPrice() {
        return super.getPrice().add(new BigDecimal(26));
    }
}
