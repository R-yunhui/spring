package com.ral.young.concurrent;

import org.openjdk.jol.info.ClassLayout;

/**
 * Synchronized 锁
 *
 * 特性：
 * 1.互斥性：同一时间只有一个线程可以获取到锁
 * 2.阻塞性：只获取到锁的线程可以执行任务，未获取到锁的线程只能阻塞等待锁的释放
 * 3.可重入性
 *
 * @author renyunhui
 * @date 2024-03-12 10:41
 * @since 1.0.0
 */
public class SynchronizedDemo {

    public static void main(String[] args) {
        /*
         * 1.EntryList：存放阻塞状态的线程，等待获取锁的线程队列
         * 2.WaitSet：存放处理 wait 状态的线程队列
         * 3.recursions：锁的可重入次数
         * 4.count：记录该线程获取锁的次数
         * 5.owner：指向持有 ObjectMonitor 对象的线程
         *
         * 解决并发的三大特性：
         * 1.原子性：同一时间只能被同一个线程操作，再锁释放之前其他线程无法操作
         * 2.有序性：同一时间只能被同一个线程操作，再锁释放之前其他线程无法操作，不用担心指令重排带来的影响
         * 3.可见性：被 synchronized 修改的代码保证再锁释放之前将修改的变量从线程的工作内存刷新到主内存
         *
         * 优化：
         * 偏向锁
         * 轻量级锁
         * 适应性自旋
         * 锁消除：需要利用 JIT编译优化 逃逸分析，某个变量不会逃逸出这个方法，就不需要考虑并发的问题，因为这个变量只会被单个线程进行操作
         * 锁粗化：避免重复多次获取锁 例如再 for 循环中尝试加锁
         */

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
