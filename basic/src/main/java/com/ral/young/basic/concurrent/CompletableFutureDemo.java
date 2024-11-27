package com.ral.young.basic.concurrent;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author renyunhui
 * @description 这是一个CompletableFutureDemo类
 * @date 2024-11-22 09-21-21
 * @since 1.0.0
 */
@Slf4j
public class CompletableFutureDemo {

    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(4, 8, 60L, TimeUnit.SECONDS
            , new LinkedBlockingDeque<>(500), ThreadFactoryBuilder.create().setNamePrefix("CompletableFutureDemo-").build(), new ThreadPoolExecutor.CallerRunsPolicy()
    );

    static {
        // 允许核心线程预热启动
        THREAD_POOL_EXECUTOR.prestartAllCoreThreads();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // testOne();

        // testTwo();

        testThree();
    }

    public static void testOne() throws ExecutionException, InterruptedException {
        // 异步执行，返回一个CompletableFuture对象
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            String result = "hello world";
            log.info("{} testOne 开始执行任务", Thread.currentThread().getName());
            try {
                // 模拟任务的执行
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return result;
        });

        /*
         * get() 和 join() 都会等待异步操作完成并返回结果，但 get() 在等待期间线程被中断时会抛出异常，而 join() 不会。
         * isDone() 用于检查异步操作是否完成，而不等待操作完成，适用于非阻塞的轮询场景。
         */
        String data = completableFuture.get();
        log.info("{} 等待异步任务执行完成之后获取任务结果: {}", Thread.currentThread().getName(), data);
    }

    public static void testTwo() {
        CompletableFuture.runAsync(() -> {
            log.info("{} start deal task", Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).thenAcceptAsync(o -> {
            // 组合在一起执行任务，等待前面的任务执行完毕之后再异步执行其他的任务
            log.info("{} : task is finish", Thread.currentThread().getName());
        });

        // 不会被第二个任务影响到
        log.info("{} 结束了", Thread.currentThread().getName());
    }

    public static void testThree() {
        // 1.打水
        // 2.烧水
        // 3.清洗茶叶
        // 4.炒茶叶
        // 5.泡茶
        // 6.喝茶
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("{} 开始打水", Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "water";
        });

        CompletableFuture<String> futureTwo = CompletableFuture.supplyAsync(() -> {
            log.info("{} 开始清洗茶叶", Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "清洗好的茶叶";
        });

        futureTwo.thenApply(o -> {
            log.info("{} , 获取到 : {} , 开始准备炒制茶叶", Thread.currentThread().getName(), o);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "清洗好的茶叶";
        });

        CompletableFuture<Void> finish = future.thenAcceptBothAsync(CompletableFuture.runAsync(() -> {
            log.info("{} 获取到水, 开始烧水", Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }), (o, u) -> {
            if (futureTwo.isDone()) {
                log.info("炒制茶叶完成 , {} 准备开始泡茶", Thread.currentThread().getName());
            }
        });

        finish.join();
        log.info("{} 喝茶", Thread.currentThread().getName());
    }
}
