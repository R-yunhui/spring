package com.ral.young.study.designpattern.structuraltype.decoratorpattern;

/**
 * 装饰者模式
 *
 * @author renyunhui
 * @date 2023-12-04 14:14
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        /*
         * 什么是装饰者模式
         * 装饰者模式（DecoratorPattern）是指在不改变原有对象的基础之上，将功能附加到对
         * 象上，提供了比继承更有弹性的替代方案（扩展原有对象的功能），属于结构型模式。
         *
         * 装饰者模式使用场景
         * 1、用于扩展一个类的功能或给一个类添加附加职责。
         * 2、动态的给一个对象添加功能，这些功能可以再动态的撤销。
         * 注：MyBatis中的二级缓存就是用了装饰者模式来进行动态扩展，感兴趣的可以去了解下
         *
         * 装饰者模式优点
         * 1、装饰者是继承的有力补充，比继承灵活，不改变原有对象的情况下动态地给一个对象 扩展功能，即插即用。
         * 2、通过使用不同装饰类以及这些装饰类的排列组合，可以实现不同效果。
         * 3、装饰者完全遵守开闭原则。
         * 装饰者模式缺点
         * 1、会出现更多的代码，更多的类，增加程序复杂性。
         * 2、动态装饰以及多层装饰时会更加复杂。
         *
         * 模拟场景：
         * 做一个蛋糕，希望可以增加不同的水果，水果的数量和种类不固定
         *
         * 对装饰器模式来说，装饰者（decorator）和被装饰者（decorate）都实现同一个 接口。
         * 对代理模式来说，代理类（proxy class）和真实处理的类（real class）都实现同一个接口。他们之间的边界确实比较模糊，两者都是对类的方法进行扩展，具体区别如下：
         * 1、装饰器模式强调的是增强自身，在被装饰之后你能够在被增强的类上使用增强后的功能。增强后你还是你，只不过能力更强了而已；
         * 代理模式强调要让别人帮你去做一些本身与你业务没有太多关系的职责（记录日志、设置缓存）。代理模式是为了实现对象的控制，因为被代理的对象往往难以直接获得或者是其内部不想暴露出来。
         * 2、装饰模式是以对客户端透明的方式扩展对象的功能，是继承方案的一个替代方案；代理模式则是给一个对象提供一个代理对象，并由代理对象来控制对原有对象的引用；
         * 3、装饰模式是为装饰的对象增强功能；而代理模式对代理的对象施加控制，但不对对象本身的功能进行增强；
         */
        Cake cake;
        cake = new BaseCake();
        System.out.println(cake.getDes() + ",价格" + cake.getPrice());

        // 加一个苹果
        cake = new AppleCakeDecorator(cake);
        System.out.println(cake.getDes() + ",价格" + cake.getPrice());

        // 加一串葡萄
        cake = new GrapeCakeDecorator(cake);
        System.out.println(cake.getDes() + ",价格" + cake.getPrice());

        // 再加一个苹果
        cake = new AppleCakeDecorator(cake);
        System.out.println(cake.getDes() + ",价格" + cake.getPrice());
    }
}
