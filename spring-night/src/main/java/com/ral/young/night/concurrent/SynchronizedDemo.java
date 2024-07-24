package com.ral.young.night.concurrent;

/**
 * Synchronized 示例
 *
 * @author renyunhui
 * @date 2024-07-24 15:12
 * @since 1.0.0
 */
public class SynchronizedDemo {

    public static void main(String[] args) {
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
        // synchronizedDemo.testOne();
        synchronizedDemo.testTwo();
    }

    public void testOne() {
        System.out.println("测试 Synchronized 锁实例对象");
        for (int i = 0, n = 10; i < n; i++) {
            TaskOne taskOne = new TaskOne();
            new Thread(taskOne).start();
        }
    }

    public void testTwo() {
        System.out.println("测试 Synchronized 锁类对象");
        for (int i = 0, n = 10; i < n; i++) {
            TaskTwo taskTwo = new TaskTwo();
            new Thread(taskTwo).start();
        }
    }

    class TaskOne implements Runnable {

        @Override
        public void run() {
            // 加锁的是当前的实例对象
            synchronized (this) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + " 执行时间：" + System.currentTimeMillis());
            }
        }
    }

    class TaskTwo implements Runnable {

        @Override
        public void run() {
            // 加锁的是类对象
            synchronized (TaskTwo.class) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + " 执行时间：" + System.currentTimeMillis());
            }
        }
    }
}
