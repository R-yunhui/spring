package com.ral.young.study.designpattern.structuraltype.decoratorpattern;

import java.math.BigDecimal;

/**
 * 蛋糕的基础类
 *
 * @author renyunhui
 * @date 2023-12-04 14:18
 * @since 1.0.0
 */
public class BaseCake extends Cake {

    public String getDes() {
        return "普通的蛋糕";
    }

    public BigDecimal getPrice() {
        return new BigDecimal(50);
    }
}
