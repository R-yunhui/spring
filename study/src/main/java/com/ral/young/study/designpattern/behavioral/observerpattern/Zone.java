package com.ral.young.study.designpattern.behavioral.observerpattern;

import java.util.ArrayList;
import java.util.List;

/**
 * 具体被观察的类，实现 Subject 接口，注册 / 移除 / 通知观察者
 * 也可以通过继承 jdk 自带的 Observable 类，简化代码
 *
 * @author renyunhui
 * @date 2023-12-04 15:45
 * @since 1.0.0
 */
public class Zone implements Subject {

    private final List<Observer> observers;

    public Zone() {
        this.observers = new ArrayList<>();
    }

    public void publishTrends(DynamicInfo dynamicInfo) {
        System.out.println(dynamicInfo.getUsername() + " 发布了一个动态【" + dynamicInfo.getDynamicData() + "】");
        // 通知所有观察者
        notifyObservers(dynamicInfo);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(Observer observer, Object arg) {

    }

    @Override
    public void notifyObservers(Object arg) {
        for (Observer observer : observers) {
            observer.update(arg);
        }
    }
}
