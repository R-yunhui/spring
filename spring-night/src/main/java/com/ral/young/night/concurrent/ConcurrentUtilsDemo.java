package com.ral.young.night.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * {@link java.util.concurrent.CountDownLatch}
 * {@link java.util.concurrent.CyclicBarrier}
 * {@link java.util.concurrent.Semaphore}
 *
 * @author renyunhui
 * @date 2024-05-27 10:41
 * @since 1.0.0
 */
public class ConcurrentUtilsDemo {

    public static void main(String[] args) throws InterruptedException {
        // CountDownLatch 是一个计数器，它允许一个或者多个线程等待其它线程完成操作。它通常用来实现一个线程等待其它多个线程完成操作之后再继续进行的操作场景
        // testCountDownLatchOne();
        // testCountDownLatchTwo();

        // Semaphore 是一个计数信号量，它允许多个线程同时访问共享资源，并通过计数器来控制访问的数量。它通过用来实现一个线程需要等待获取一个许可才能访问共享资源，或者需要释放一个许可才能完成操作的操作场景
        // testSemaphoreOne();
        // testSemaphoreTwo();

        // CyclicBarrier 是一个同步屏障，它允许一组线程相互等待，直到到达一个公共屏障点。它通过用来实现一个线程需要等待其它线程完成操作才能继续执行的场景，类似于 CountDownLatch
        testCyclicBarrier();
    }

    private static final CountDownLatch countDownLatchOne = new CountDownLatch(10);

    private static final CountDownLatch countDownLatchTwo = new CountDownLatch(1);

    private static final Semaphore semaphoreOne = new Semaphore(3);

    private static final Semaphore semaphore1 = new Semaphore(1);

    private static final Semaphore semaphore2 = new Semaphore(1);

    private static final Semaphore semaphore3 = new Semaphore(1);

    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(5, () -> {
        // 会被最后一个将 parties 减到 0 的线程所执行，执行完成之后所有阻塞的线程被唤醒开始执行
        System.out.println(Thread.currentThread().getName() + "，所有任务开始执行");
    });

    /**
     * 模拟主线程等待其它工作线程执行完毕之后再执行
     * @throws InterruptedException 中断异常
     */
    public static void testCountDownLatchOne() throws InterruptedException {
        System.out.println("testCountDownLatchOne");
        for (int i = 0, n = 10; i < n; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 准备开始执行任务");

                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " 任务执行完毕");

                    // 工作线程调用 countDownLatch() api ，每次将计数器 - 1，直到减到 0 ，就会去尝试唤醒被 await() 方法所阻塞的线程
                    countDownLatchOne.countDown();
                    System.out.println(Thread.currentThread().getName() + " 计数器数量：" + countDownLatchOne.getCount());
                } catch (Exception e) {
                    System.out.println("执行异常,e" + e);
                }
            }).start();
        }

        // 主线程调用 await() api 需要等到计数器为 0，才会继续执行
        System.out.println(Thread.currentThread().getName() + " 准备开始执行任务");
        countDownLatchOne.await();
        System.out.println(Thread.currentThread().getName() + " 任务执行完毕");
    }

    /**
     * 通过 CountDownLatch 实现多个线程等待一个线程执行完毕，人为制造并发
     * @throws InterruptedException 中断异常
     */
    public static void testCountDownLatchTwo() throws InterruptedException {
        for (int i = 0, n = 10; i < n; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 准备开始执行任务");

                    countDownLatchTwo.await();

                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " 任务执行完毕");
                } catch (Exception e) {
                    System.out.println("执行异常,e" + e);
                }
            }).start();
        }

        Thread.sleep(3000);
        System.out.println(Thread.currentThread().getName() + " 任务执行完毕，允许子线程开始执行任务");
        countDownLatchTwo.countDown();
    }

    /**
     * 模拟 10 个线程同时访问某个资源，只有获取到许可的线程可以访问成功，其它线程需要等待获取许可的线程释放许可之后才可以执行任务
     * @throws InterruptedException 中断异常
     */
    public static void testSemaphoreOne() throws InterruptedException {
        // 1.先利用 CountDownLatch 制造并发
        for (int i = 0, n = 10; i < n; i++) {
            new Thread(() -> {
                try {
                    countDownLatchTwo.await();

                    executeTask();
                } catch (Exception e) {
                    System.out.println("执行异常," + e);
                }
            }).start();
        }

        Thread.sleep(3000);
        System.out.println(Thread.currentThread().getName() + " 任务执行完毕，允许子线程开始执行任务");
        countDownLatchTwo.countDown();
    }

    private static void executeTask() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " 准备开始执行任务，当前剩余许可：" + semaphoreOne.availablePermits());
        if (semaphoreOne.tryAcquire(1)) {
            Thread.sleep(1000);
            // 执行完成之后释放许可
            semaphoreOne.release();
            System.out.println(Thread.currentThread().getName() + " 任务执行完毕。释放许可，当前剩余许可：" + semaphoreOne.availablePermits());
        } else {
            // 休眠 500ms 后尝试重试获取许可，直到任务执行完成
            Thread.sleep(500);
            System.out.println(Thread.currentThread().getName() + " 获取许可失败，重试再次获取许可执行任务");
            executeTask();
        }
    }

    /**
     * 使用 Semaphore 实现循环顺序打印 1 2 3 100次
     * @throws InterruptedException 中断异常
     */
    public static void testSemaphoreTwo() throws InterruptedException {
        // 一开始就获取 2 和 3 的许可，保证之后 1 号线程的任务可以执行成功
        semaphore2.acquire();
        semaphore3.acquire();

        Thread thread1 = new Thread(() -> {
            for (int i = 0, n = 100; i < n; i++) {
                try {
                    semaphore1.acquire();
                    System.out.print(1);

                    // 释放 2 的许可
                    semaphore2.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0, n = 100; i < n; i++) {
                try {
                    semaphore2.acquire();
                    System.out.print(2);

                    // 释放 3 的许可
                    semaphore3.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread thread3 = new Thread(() -> {
            for (int i = 0, n = 100; i < n; i++) {
                try {
                    semaphore3.acquire();
                    System.out.print(3);
                    System.out.println("   顺序循环打印 123 执行次数" + (i + 1));
                    // 释放 1 的许可
                    semaphore1.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
    }

    /**
     * 使用 CyclicBarrier 实现多个线程相互等待，直到到达某个状态，所有线程一起开始执行
     * @throws InterruptedException 中断异常
     */
    public static void testCyclicBarrier() throws InterruptedException {
        for (int i = 0, n = 5; i < n; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 准备开始执行任务");
                    cyclicBarrier.await();

                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " 任务执行完毕");
                } catch (Exception e) {
                    System.out.println("执行异常," + e);
                }
            }).start();
        }
    }
}

