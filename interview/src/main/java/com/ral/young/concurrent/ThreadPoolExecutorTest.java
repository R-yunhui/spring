package com.ral.young.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.ThreadPoolExecutor}
 *
 * @author renyunhui
 * @date 2023-02-09 17:21
 * @since 1.0.0
 */
@Slf4j
public class ThreadPoolExecutorTest {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executorOne = ThreadPoolUtil.createPoolExecutor(2, 6, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), true);
        CompletableFuture.runAsync(() -> {
            // 可以进行线程预热，初始化核心线程数
            executorOne.prestartAllCoreThreads();
            // 配置运行核心线程数运行在配置的空闲时间内未运行则被回收
            executorOne.allowCoreThreadTimeOut(true);

            for (int i = 0, n = 10; i < n; i++) {
                executorOne.execute(() -> {
                    log.info("{}:开始执行第一个任务", Thread.currentThread().getName());
                    try {
                        // 模拟线程执行任务很久的情况
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("{}:第一个执行任务结束", Thread.currentThread().getName());
                });
            }
        });

        ThreadPoolExecutor executorTwo = ThreadPoolUtil.createPoolExecutor(2, 6, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), "custom", true);
        CompletableFuture.runAsync(() -> {
            for (int i = 0, n = 10; i < n; i++) {
                executorTwo.execute(() -> {
                    log.info("{}:开始第二个执行任务", Thread.currentThread().getName());
                    try {
                        // 模拟线程执行任务很久的情况
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("{}:第二个执行任务结束", Thread.currentThread().getName());
                });
            }
        });
    }
}
