package com.ral.young.study.designpattern.behavioral.templatemethodpattern;

/**
 * 模板方法模式
 *
 * @author renyunhui
 * @date 2023-12-04 14:32
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        /*
         * 什么是模板方法模式
         * 模板方法模式（Template Method Pattern）是指定义一个算法的骨架，并允许子类为一个或者多个步骤提供实现。
         * 模板方法使得子类可以在不改变算法结构的情况下，重新定义算法的某些步骤，属于行为行设计模式。
         *
         * 模板方法模式应用场景
         * 一次性实现一个算法的不变的部分，并将可变的行为留给子类来实现。
         * 各子类中公共的行为被提取出来并集中到一个公共的父类中，从而避免代码重复。
         *
         * 模板方法模式优点
         * 利用模板方法将相同处理逻辑的代码放到抽象父类中，可以提高代码的复用性。
         * 将不同的代码不同的子类中，通过对子类的扩展增加新的行为，提高代码的扩展性。
         * 把不变的行为写在父类上，去除子类的重复代码，提供了一个很好的代码复用平台， 符合开闭原则。
         *
         * 模板方法模式缺点
         * 类数目的增加，每一个抽象类都需要一个子类来实现，这样导致类的个数增加。
         * 类数量的增加，间接地增加了系统实现的复杂度。
         * 继承关系自身缺点，如果父类添加新的抽象方法，所有子类都要改一遍。
         *
         * 实际应用场景：
         * Spring的事务管理：
         * AbstractPlatformTransactionManager 定义了具体的事务处理逻辑，比如 commit() 和 rollback()，
         * 但是由子类自己实现具体的事务处理逻辑 doBegin() 方法
         */
        CookingChickenTemplate cookingChickenTemplate = new CookingChickenTemplate();
        cookingChickenTemplate.cooking();
        System.out.println();

        CookingPotatoTemplate cookingPotatoTemplate = new CookingPotatoTemplate();
        cookingPotatoTemplate.cooking();
    }
}
