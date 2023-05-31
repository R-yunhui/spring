package com.ral.young.basic.oncurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link java.util.concurrent.CountDownLatch}
 *
 * CountDownLatch这个类能够使一个或多个线程等待其他线程完成各自的工作后再执行。例如，应用程序的主线程希望在负责启动框架服务的线程已经启动所有的框架服务之后再执行。
 *
 * CountDownLatch 是通过一个计数器来实现的，计数器的初始值为允许访问资源的任务线程数量。每当一个线程完成了自己的任务后，计数器的值就会减 1。
 * 当计数器值到达 0 时，它表示所有的线程已经完成了任务，然后在唤醒等待的线程就可以恢复执行任务。
 *
 * 场景1：让单个线程等待。
 * 场景2：和多个线程等待。
 *
 * @author renyunhui
 * @date 2023-05-29 16:38
 * @since 1.0.0
 */
public class CountDownLatchDemo {

    private final CountDownLatch countDownLatchOne = new CountDownLatch(10);

    private final CountDownLatch countDownLatchTwo = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        CountDownLatchDemo demo = new CountDownLatchDemo();
        // demo.testCountDownLatchOne();
        demo.testCountDownLatchTwo();
    }

    private void testCountDownLatchOne() throws InterruptedException {
        // 模拟场景一，一个线程等待多个线程执行完毕
        for (int i = 0, n = 10; i < n; i++) {
            Thread t1 = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "：获取到锁,开始执行任务");
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000) + 300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + "：任务执行完毕");
                countDownLatchOne.countDown();
            });

            t1.start();
        }

        System.out.println("主线程等待子线程任务执行完毕");
        countDownLatchOne.await();
        System.out.println("子线程任务执行完毕,主线程开始执行任务");
    }

    private void testCountDownLatchTwo() throws InterruptedException {
        // 模拟场景二，多个线程等待一个线程执行完毕才开始执行任务
        for (int i = 0, n = 10; i < n; i++) {
            Thread t1 = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "：等待裁判发令");
                try {
                    countDownLatchTwo.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + "：起跑");
            });

            t1.start();
        }

        Thread.sleep(2000);
        countDownLatchTwo.countDown();
        System.out.println("裁判发令,start go");
    }
}
