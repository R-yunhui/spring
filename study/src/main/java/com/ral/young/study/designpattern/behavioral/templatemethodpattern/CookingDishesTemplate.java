package com.ral.young.study.designpattern.behavioral.templatemethodpattern;

/**
 * 菜品烹饪流程
 *
 * @author renyunhui
 * @date 2023-12-04 15:13
 * @since 1.0.0
 */
public abstract class CookingDishesTemplate {

    public void cooking() {
        // 烹饪流程
        addOil();
        putVegetables();
        stirFry();
        putSalt();

        if (needSeasoning()) {
            // 子类自定义实现逻辑
            putSeasoning();
        }
    }

    public final void addOil() {
        System.out.println("倒入香油下锅。。。");
    }

    public final void putVegetables() {
        System.out.println("菜下锅了。。。");
    }

    public final void stirFry() {
        System.out.println("正在翻炒。。。");
    }

    public final void putSalt() {
        System.out.println("放点盐。。。");
    }

    /**
     * 添加其它佐料的模板方法，交由具体的子类实现
     */
    public abstract void putSeasoning();

    /**
     * 是否需要添加佐料
     * @return 是否需要添加
     */
    public abstract boolean needSeasoning();
}
