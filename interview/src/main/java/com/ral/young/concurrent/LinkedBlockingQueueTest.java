package com.ral.young.concurrent;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.LinkedBlockingQueue}
 *
 * @author renyunhui
 * @date 2023-02-10 10:44
 * @since 1.0.0
 */
public class LinkedBlockingQueueTest {

    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024), ThreadFactoryBuilder.create().setNamePrefix("work-%d").build());
        ThreadPoolExecutor executorTwo = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024), ThreadFactoryBuilder.create().setNamePrefix("work-%d").build());
        queue.put(ThreadLocalRandom.current().nextInt(1000000));
        CompletableFuture.runAsync(() -> {
            for (int i = 0, n = 10; i < n; i++) {
                executor.execute(() -> {
                    try {
                        queue.put(ThreadLocalRandom.current().nextInt(1000000));
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println("添加成功:" + queue.size());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        CompletableFuture.runAsync(() -> {
            for (int i = 0, n = 10000; i < n; i++) {
                executorTwo.execute(() -> {
                    try {
                        System.out.println("尝试获取元素");
                        TimeUnit.SECONDS.sleep(1);
                        Integer take = queue.take();
                        System.out.println("获取成功:" + take + " " + queue.size());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        Thread.sleep(100000, TimeUnit.MINUTES.ordinal());
    }
}
