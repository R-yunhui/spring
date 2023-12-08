package com.ral.young.study.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * {@link CountDownLatch}
 *
 * @author renyunhui
 * @date 2023-12-06 14:50
 * @since 1.0.0
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        testTwo();
    }

    public static void testOne() throws InterruptedException {
        // CountDownLatch 类似与门栓，只有门栓消失，才能进入
        // 初始化 5 个门栓
        // 正向使用
        int size = 5;
        CountDownLatch countDownLatch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 减少门栓数量
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName() + "，打开了门栓");
            }).start();
        }

        // 门栓存在就无法执行后续的操作
        System.out.println("当前门栓数量：" + countDownLatch.getCount());
        countDownLatch.await();
        System.out.println("主线程开始执行任务");
    }

    public static void testTwo() throws InterruptedException {
        // 反向使用 CountDownLatch
        // 多个线程需要访问资源，门栓只有一个，等待门栓的打开
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "，等待放行执行任务");
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + "，开始执行任务");
            }).start();
        }

        Thread.sleep(3000);
        countDownLatch.countDown();
        System.out.println(Thread.currentThread().getName() + "，放行");
    }
}
