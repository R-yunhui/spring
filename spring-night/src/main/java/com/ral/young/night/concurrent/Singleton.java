package com.ral.young.night.concurrent;

/**
 * 实现单例模式
 *
 * @author renyunhui
 * @date 2024-07-24 15:28
 * @since 1.0.0
 */
public class Singleton {

    private static volatile Singleton instance;

    private Singleton() {
    }

    public static Singleton getInstance() {
        // 使用 volatile 防止指令重排，导致获取到的 instance 还么有经过初始化是 null
        if (null == instance) {
            synchronized (instance) {
                if (null == instance) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
