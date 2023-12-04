package com.ral.young.study.designpattern.behavioral.observerpattern;

/**
 * 被观察者实现此接口，实现具体的方法
 * 注册 / 移除 / 通知具体的观察者
 *
 * @author renyunhui
 * @date 2023-12-04 15:52
 * @since 1.0.0
 */
public interface Subject {

    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObserver(Observer observer, Object arg);

    void notifyObservers(Object arg);
}
