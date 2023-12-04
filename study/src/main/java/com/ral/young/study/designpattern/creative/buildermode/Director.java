package com.ral.young.study.designpattern.creative.buildermode;

import cn.hutool.json.JSONUtil;

/**
 * 调用者(Director)：调用具体的建造者来创建各个对象的各个部分
 *
 * @author renyunhui
 * @date 2023-12-04 9:46
 * @since 1.0.0
 */
public class Director {

    public static void main(String[] args) {
        /*
         * 建造者模式适用于一个具有较多的零件的复杂产品创建过程，而且产品的各个组成零件还会经常发生变化或者说需要支持动态变化，但是零件的种类却总体稳定的场景：
         *
         *  1、相同的方法，不同的执行顺序需要产生不同的执行结果
         *  2、产品类非常复杂，调用不同的零件或者按照不同的顺序来组装产品后需要得到不同的产品
         *  3、当初始化一个对象非常复杂，而且很多参数都具有默认值
         *
         * 实际使用场景：
         * StringBuilder.append();
         * Mybatis：XMLConfigBuilder ， XMLMapperBuilder . XMLStatementBuilder
         *
         * 建造者模式的优点有：
         * 1、封装性好，创建和使用分离
         * 2、扩展性好，建造类之间独立，一定程度上实现了解耦
         *
         * 建造者模式的缺点有：
         * 1、产生多余的Builder对象
         * 2、产品内部发生变化时，建造者都需要修改，成本较大
         *
         * 建造者模式优点类似于工厂模式，都是用来创建一个对象，但是他们还是有很大的区别，主要区别如下：
         * 1、建造者模式更加注重方法的调用顺序，工厂模式注重于创建完整对象
         * 2、建造者模式根据不同的产品零件和顺序可以创造出不同的产品（种类不变），而工厂模式创建出来的产品都是一样的
         * 3、建造者模式使用者需要知道这个产品有哪些零件组成，而工厂模式的使用者不需要知道，直接创建就行
         */

        ConcreteBuilder concreteBuilder = new ConcreteBuilder();
        // 实际建造者的调用过程，可以通过不同的组合创建相同种类的对象
        BusinessSpace businessSpace = concreteBuilder.buildName("测试业务空间")
                .buildTemplateA("模板A")
                .buildTemplateB("模板B")
                .build();
        System.out.println(JSONUtil.toJsonPrettyStr(businessSpace));
    }
}
