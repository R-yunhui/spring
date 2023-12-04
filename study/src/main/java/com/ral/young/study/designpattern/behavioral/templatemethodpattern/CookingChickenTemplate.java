package com.ral.young.study.designpattern.behavioral.templatemethodpattern;

/**
 * 具体的模板方法子类，实现模板方法的具体逻辑
 *
 * @author renyunhui
 * @date 2023-12-04 15:21
 * @since 1.0.0
 */
public class CookingChickenTemplate extends CookingDishesTemplate {

    @Override
    public void putSeasoning() {
        System.out.println("鸡肉下锅了。。。");
    }

    @Override
    public boolean needSeasoning() {
        return true;
    }
}
