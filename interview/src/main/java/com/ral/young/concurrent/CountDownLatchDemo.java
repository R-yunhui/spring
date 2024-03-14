package com.ral.young.concurrent;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.CountDownLatch}
 *
 * @author renyunhui
 * @date 2024-03-13 10:55
 * @since 1.0.0
 */
@Slf4j
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatchDemo demo = new CountDownLatchDemo();
        // demo.testOne();
        demo.testTwo();
    }

    ThreadPoolExecutor executorOne = new ThreadPoolExecutor(5, 10, 1L, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder().setNameFormat("test-one-%d").get());

    ThreadPoolExecutor executorTwo = new ThreadPoolExecutor(5, 10, 1L, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder().setNameFormat("test-two-%d").get());

    final CountDownLatch countDownLatchOne = new CountDownLatch(1);

    final CountDownLatch countDownLatchTwo = new CountDownLatch(5);


    public void testOne() throws InterruptedException {
        // 使用 CountDownLatch，多线程等待释放的信号，一起开始执行任务
        for (int i = 0, n = 5; i < n; i++) {
            executorOne.execute(() -> {
                log.info(Thread.currentThread().getName() + "：开始准备工作");

                try {
                    countDownLatchOne.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                log.info(Thread.currentThread().getName() + "：开始执行任务");
            });
        }

        Thread.sleep(5000);

        log.info("准备工作准备完成.....  开始执行任务");
        countDownLatchOne.countDown();

        executorOne.shutdown();
    }

    public void testTwo() throws InterruptedException {
        // 多线程执行任务，全部执行完成之后，另外的线程开始允许
        for (int i = 0, n = 5; i < n; i++) {
            executorTwo.execute(() -> {
                log.info(Thread.currentThread().getName() + "：开始执行任务");
                // ......
                countDownLatchTwo.countDown();
            });
        }

        // 主线程等待子线程执行完毕之后开始允许
        log.info("等待所有成员执行完成.....");
        countDownLatchTwo.await();
        log.info("所有成员执行任务完成，开始执行自己的任务");

        executorTwo.shutdown();
    }
}
