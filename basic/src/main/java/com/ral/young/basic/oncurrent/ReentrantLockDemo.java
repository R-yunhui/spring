package com.ral.young.basic.oncurrent;

import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link java.util.concurrent.locks.ReentrantLock}
 *
 * @author renyunhui
 * @date 2023-05-29 10:00
 * @since 1.0.0
 */
public class ReentrantLockDemo {

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private int state = 1;

    public static void main(String[] args) throws InterruptedException {
        interrupt();

        printNum();
    }

    /**
     * 测试 lock 锁对于线程中断的支持
     * lock.lockInterruptibly()
     */
    private static void interrupt() throws InterruptedException {
        ReentrantLockDemo demo = new ReentrantLockDemo();
        Thread t0 = new Thread(demo::testReentrantLockInterruptOne);
        Thread t1 = new Thread(demo::testReentrantLockInterruptTwo);
        t0.start();
        t1.start();

        // 尝试给 t1 发送中断信号
        Thread.sleep(10000);
        if (t1.isAlive()) {
            /*
             * 在使用 lock.lock(); 的方式的时候，t1 线程不能主动感知到线程的中断
             * 如果通过 lockInterruptibly(); 的方式，后续在获取锁的过程中会抛出线程中断异常，尝试获取锁的线程任务可以针对此异常进行补偿操作
             * 通过显示锁 Lock 的 lockInterruptibly 方法来完成，它和 lock 方法作用类似，但是 lockInterruptibly 可以优先接收到中断的通知，而 lock 方法只能“死等”锁资源的释放
             */
            t1.interrupt();
            System.out.println("主线程主动中断 t1 线程");
        }
    }

    private static void printNum() {
        ReentrantLockDemo demo = new ReentrantLockDemo();
        Thread t1 = new Thread(() -> {
            demo.testReentrantLock(1);
        });

        Thread t2 = new Thread(() -> {
            demo.testReentrantLock(2);
        });

        Thread t3 = new Thread(() -> {
            demo.testReentrantLock(3);
        });

        t1.start();
        t2.start();
        t3.start();
    }

    /**
     * 多线程循环打印指定的数字
     *
     * @param num 指定的数字
     */
    private void testReentrantLock(int num) {
        /*
         * ReentrantLock 依赖 aqs 的 state 来保证可见性，因为 state 是由 volatile 修饰的，
         * 无论是加锁还是释放锁，都会读取state的值并进行CAS修改操作，由于volatile保证的全体可见性，在修改state时能够拿到最新的共享变量的值，
         * 并根据锁定义互斥性的只有一个线程获得操纵权，从而保证了可见性，获得锁的线程拿到的一定是最新的数据。
         *
         * 【注】：一个volatile变量的写操作发生在这个volatile变量随后的读操作之前
         * 线程A 在写入state变量之前的任何操作结果对 线程B 都是可见的
         */
        for (; ; ) {
            try {
                reentrantLock.lock();
                if (state == num) {
                    System.out.print(num + " ");

                    if (state == 3) {
                        state = 1;
                        System.out.println();
                    } else {
                        state++;
                    }
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }


    private void testReentrantLockInterruptOne() {
        System.out.println(Thread.currentThread().getName() + "：尝试获取锁");
        reentrantLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "：或取到锁");
        } finally {
            // 线程 1 不释放锁
        }
    }

    private void testReentrantLockInterruptTwo() {
        // 先休眠 0.5s，让线程 1 先执行
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "：尝试获取锁");
        try {
            reentrantLock.lockInterruptibly();
            System.out.println(Thread.currentThread().getName() + "：或取到锁");
        } catch (InterruptedException e) {
            // 在尝试获取锁的过程中
            System.out.println(Thread.currentThread().getName() + ": 被中断了");
            Thread.currentThread().interrupt();
        } finally {
            reentrantLock.unlock();
        }
    }
}
