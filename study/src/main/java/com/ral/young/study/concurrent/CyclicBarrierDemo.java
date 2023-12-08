package com.ral.young.study.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link java.util.concurrent.CyclicBarrier}
 *
 * @author renyunhui
 * @date 2023-12-06 15:09
 * @since 1.0.0
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException {
        testOne();
    }

    public static void testOne() throws InterruptedException {
        // CyclicBarrier 循环屏障，支持循环使用的
        int size = 5;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(size, () -> System.out.println(Thread.currentThread().getName() + "，屏障消失，准备开始！！！"));

        for (int i = 0; i < size; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "，到达起点，准备起跑");
                    // 屏障数量--
                    cyclicBarrier.await();
                    Thread.sleep(500 + ThreadLocalRandom.current().nextInt(1000));
                    // 降低到 0，就被唤醒可以启动
                    System.out.println(Thread.currentThread().getName() + "，启动！！！");
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        Thread.sleep(5000);
    }
}
