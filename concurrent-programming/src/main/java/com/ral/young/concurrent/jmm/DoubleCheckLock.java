package com.ral.young.concurrent.jmm;

/**
 * 单例模式：double check 实现单例模式
 *
 * @author renyunhui
 * @date 2022-09-13 12:05
 * @since 1.0.0
 */
public class DoubleCheckLock {

    private static volatile DoubleCheckLock instance;

    private DoubleCheckLock getInstance() {
        // 第一次检测
        if (null == instance) {
            // 同步加锁
            synchronized (DoubleCheckLock.class) {
                // 第二次检测
                if (null == instance) {
                    /*
                     * 多线程情况下可能出现问题的地方 - 有序性
                     * new 对象的过程：
                     * 1.给对象分配内存
                     * 2.初始化对象
                     * 3.设置 instance 指向前面分配的内存地址，此时 instance != null
                     *
                     * 编译器优化：
                     * 1.给对象分配内存
                     * 2.设置 instance 指向前面分配的内存地址，此时 instance != null，但是对象并没有初始化完成
                     * 3.初始化对象
                     *
                     * 可以使用 volatile 修饰，保证有序性
                     */
                    instance = new DoubleCheckLock();
                }
            }
        }
        return instance;
    }
}
