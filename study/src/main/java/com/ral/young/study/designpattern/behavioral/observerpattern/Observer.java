package com.ral.young.study.designpattern.behavioral.observerpattern;

/**
 * 观察者接口
 *
 * @author renyunhui
 * @date 2023-12-04 15:53
 * @since 1.0.0
 */
public interface Observer {

    /**
     * 观察的对象发生变化，回调此方法通知观察者
     * @param arg 具体的变化
     */
    void update(Object arg);
}
