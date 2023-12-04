package com.ral.young.study.designpattern.behavioral.observerpattern;

import cn.hutool.core.date.DateUtil;

import java.util.Collections;

/**
 *
 * @author renyunhui
 * @date 2023-12-04 15:29
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        /*
         * 什么是观察者模式
         * 观察者模式（Observer Pattern）定义了对象之间的一对多依赖，让多个观察者对象同时监听一个主体对象，
         * 当主体对象发生变化时，它的所有依赖者（观察者）都会收到通 知并更新，属于行为型模式。观察者模式有时也叫做发布订阅模式，
         * 主要用 于在关联行为之间建立一套触发机制的场景
         *
         * 可以选择被观察者主动向观察者推送数据或者观察者向被观察者主动去拉取数据
         *
         * 可以使用 jdk 自带的观察者接口和类
         * java.util.Observer
         * java.util.Observable
         *
         * 使用 jdk 自带的观察者的缺陷：
         * 1、Observable是一个类而不是一个接口，所以就限制了它的使用和服用，如果某类同时想具有Observable类和另一个超类的行为，就会有问题，毕竟java不支持多继承。
         * 2、Observable将关键的方法保护起来了，比如setChanged()方法，这意味着除非我们继承自Observable，否则无法创建Observable实例并组合到我们自己的对象中来，这个设计违反了设计原则：多用组合，少用继承。
         *
         * 观察者模式的有优点
         * 1、观察者和被观察者之间建立了一个抽象的耦合。
         * 2、观察者模式支持广播通信
         *
         * 观察者模式的有缺点
         * 1、观察者之间有过多的细节依赖、提高时间消耗及程序的复杂度。
         * 2、使用要得当，要避免循环调用。
         */
        Zone zone = new Zone();
        DynamicInfo dynamicInfo = new DynamicInfo();
        dynamicInfo.setUsername("mike");
        dynamicInfo.setDynamicData("世界杯梅西夺冠！！！");
        dynamicInfo.setPublichDate(DateUtil.date());
        // 添加观察者
        zone.addObserver(new WeChatUser(Collections.singletonList("bob")));

        // 发布事件变更，告知观察者被观察的对象发生变化
        zone.publishTrends(dynamicInfo);
    }
}
