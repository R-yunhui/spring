package com.ral.young.night.jvm;

import lombok.extern.slf4j.Slf4j;

/**
 * 模拟死锁的情况
 *
 * @author renyunhui
 * @date 2024-06-25 14:37
 * @since 1.0.0
 */
@Slf4j
public class DeadLockDemo {

    public static void main(String[] args) throws InterruptedException {
        DeadLockDemo demo = new DeadLockDemo();
        new Thread(demo::dealTaskOne).start();
        new Thread(demo::dealTaskTwo).start();

        Thread.sleep(100000);
    }

    public static Object lockOne = new Object();

    public static Object lockTwo = new Object();

    public void dealTaskOne() {
        synchronized (lockTwo) {
            System.out.println("dealTaskOne");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("执行异常：", e);
            }

            synchronized (lockOne) {
                System.out.println("dealTaskOne");
            }
        }
    }

    public void dealTaskTwo() {
        synchronized (lockOne) {
            System.out.println("dealTaskTwo");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("执行异常：", e);
            }

            synchronized (lockTwo) {
                System.out.println("dealTaskTwo");
            }
        }
    }
}
