package com.ral.young.concurrent;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link java.util.concurrent.CyclicBarrier} 可循环使用的屏障
 *
 * @author renyunhui
 * @date 2023-02-08 17:02
 * @since 1.0.0
 */
@Slf4j
public class CyclicBarrierTest {

    static CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> log.info("所有障碍已经消除,开始执行任务"));

    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 8, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), ThreadFactoryBuilder.create().setNameFormat("work-thread-%d").get());

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        log.info("现在的阻碍数量:{}", cyclicBarrier.getParties());
        for (int i = 0, n = 3; i < n; i++) {
            threadPoolExecutor.execute(() -> {
                try {
                    log.info("线程:{},开始做准备", Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(atomicInteger.getAndIncrement() * 5) + 3);
                    log.info("线程:{},准备完毕,现在的线程等待数量:{}", Thread.currentThread().getName(), cyclicBarrier.getNumberWaiting());
                    // 消除阻碍，等待不存在阻碍之后，所有线程开始执行 await 的后续的逻辑
                    cyclicBarrier.await();
                    log.info("线程:{},开始执行任务", Thread.currentThread().getName());
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        threadPoolExecutor.shutdown();
    }
}
