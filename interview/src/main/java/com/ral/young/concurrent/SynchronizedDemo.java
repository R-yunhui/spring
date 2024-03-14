package com.ral.young.concurrent;

import org.openjdk.jol.info.ClassLayout;

/**
 * Synchronized 锁
 *
 * @author renyunhui
 * @date 2024-03-12 10:41
 * @since 1.0.0
 */
public class SynchronizedDemo {

    public static void main(String[] args) {
        SynchronizedDemo demo = new SynchronizedDemo();
        testSynchronizedOne();

        demo.testSynchronizedTwo();

        testSynchronizedThree();

        // 测试锁升级过程
        demo.testLockUpgrades();
    }

    static final Object o = new Object();

    public static void testSynchronizedOne() {
        synchronized (o) {
            System.out.println("同步代码块加锁成功");
        }

        System.out.println("退出同步代码块");
    }

    public synchronized void testSynchronizedTwo() {
        System.out.println("实例方法加锁成功");
    }

    public static synchronized void testSynchronizedThree() {
        System.out.println("静态方法加锁成功");
    }

    public void testLockUpgrades() {
        Thread t1 = new Thread(new Task());
        Thread t2 = new Thread(new Task());
        t1.start();
        t2.start();
    }

    public class Task implements Runnable {

        final Object object = new Object();

        @Override
        public void run() {
            synchronized (object) {
                System.out.println("同步代码块加锁成功，获取到锁的线程：" + Thread.currentThread().getName());
                System.out.println("当前锁的状态：" + ClassLayout.parseInstance(object).toPrintable());
            }
        }
    }
}
