package com.ral.young.basic.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * {@link java.util.concurrent.CyclicBarrier}
 * <p>
 * 可以循环使用的屏障
 * N 个线程相互等待，任何一个线程完成之前，所有的线程都必须等待。
 * <p>
 * Cyclicbarrier 的 await() 方法每被调用一次，计数便会减少 1，并阻塞住当前线程。当计数减至 0 时，阻塞解除，
 * 所有在此 CyclicBarrier 上面阻塞的线程开始运行。在这之后，如果再次调用 await() 方法，计数就又会变成 N-1，新一轮重新开始，这便是 Cyclicbarrier 的含义所在。
 * <p>
 * CyclicBarrier.await() 方法带有返回值，用来表示当前线程是第几个到达这个 Barrier 的线程。
 *
 * @author renyunhui
 * @date 2023-05-30 9:13
 * @since 1.0.0
 */
public class CyclicBarrierDemo {

    private CyclicBarrier cyclicBarrier;

    public static void main(String[] args) {
        CyclicBarrierDemo demo = new CyclicBarrierDemo();
        demo.cyclicBarrierTest();
    }

    private void cyclicBarrierTest() {
        // 初始化 10 个栅栏以及一个当栅栏没有的时候执行的异步任务
        this.cyclicBarrier = new CyclicBarrier(10, () -> {
            System.out.println(Thread.currentThread().getName() + "：栅栏数量为 0,所有线程可以开始执行任务");
        });

        for (int i = 0, n = 10; i < n; i++) {
            Thread t1 = new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "：等待其它线程到达一起执行任务");
                    int await = cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName() + "：第" + await + "个到达栅栏的线程开始执行任务");
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });

            t1.start();
        }
    }
}
