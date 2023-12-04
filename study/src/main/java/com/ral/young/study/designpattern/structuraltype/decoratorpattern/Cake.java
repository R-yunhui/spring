package com.ral.young.study.designpattern.structuraltype.decoratorpattern;

import java.math.BigDecimal;

/**
 * 蛋糕的抽象类
 *
 * @author renyunhui
 * @date 2023-12-04 14:19
 * @since 1.0.0
 */
public abstract class Cake {

    public abstract String getDes();

    public abstract BigDecimal getPrice();
}
