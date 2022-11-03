package com.ral.young.concurrent.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * {@link java.util.concurrent.CountDownLatch}
 *
 * @author renyunhui
 * @date 2022-09-22 15:54
 * @since 1.0.0
 */
@Slf4j
public class CountDownLatchTest {

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(5);

    private static final ArrayBlockingQueue<String> BLOCKING_QUEUE = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                COUNT_DOWN_LATCH.await();
                log.info("其他任务已经执行完毕,执行完毕的线程名称;{},主任务开始执行", BLOCKING_QUEUE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        for (int i = 0, size = 5; i < size; i++) {
            new Thread(() -> {
                log.info("{},开始执行任务", Thread.currentThread().getName());
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                log.info("{},任务执行完毕", Thread.currentThread().getName());
                BLOCKING_QUEUE.add(Thread.currentThread().getName());
                COUNT_DOWN_LATCH.countDown();
            }).start();
        }
    }
}
