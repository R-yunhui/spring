package com.ral.young.night.concurrent;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 主线程如何获取子线程抛出的异常
 *
 * @author renyunhui
 * @date 2024-05-28 9:49
 * @since 1.0.0
 */
public class ThreadExceptionDemo {

    public static void main(String[] args) {
        /*
         * 1.通过 submit() -> future.get()
         * 2.设置线程异常处理器 UncaughtExceptionHandler
         * 3.通过 CompleteFuture 的 exceptionally() or handle 方法
         * 4.自定义线程池，继承 ThreadPoolExecutor 重写 afterExecute 方法 捕获异常进行处理
         * 5.基本的 try catch
         */

        // testOne();
        // testTwo();
        // testThree();
        testFour();
    }

    public static void testOne() {
        Thread t1 = new Thread(new Task());

        try {
            // 通过这种方式，主线程无法获取到子线程执行过程中抛出的异常
            t1.start();
        } catch (Exception e) {
            System.out.println("主线程捕获到子线程的异常");
        }
        System.out.println("主线程任务执行完毕");
    }

    public static void testTwo() {
        // 使用 Future
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 2, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10));
        Future<?> future = poolExecutor.submit(new Task());

        try {
            // 通过 future.get 阻塞的方式获取异常
            future.get();
        } catch (Exception e) {
            System.out.println("主线程捕获到子线程的异常");
        }

        poolExecutor.shutdown();
        System.out.println("主线程执行完毕");
    }

    public static void testThree() {
        // 使用 Future
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 2, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), ThreadFactoryBuilder.create()
                // 设置自定义的线程异常处理器，线程执行异常之后自己会进行异常信息的处理
                .setUncaughtExceptionHandler(new CustomExceptionHandler()).setNamePrefix("test-thread-").build());
        poolExecutor.execute(new Task());
        poolExecutor.shutdown();
        System.out.println("主线程执行完毕");
    }

    public static void testFour() {
        // 使用 CompleteFuture
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(new Task());

        // 捕获异常
        completableFuture.handle((result, exception) -> {
            if (exception != null) {
                System.out.println(Thread.currentThread().getName() + " 捕获到子线程执行异常：" + exception.getMessage());
            }
            return result;
        });
        System.out.println("主线程执行完毕");
    }

    public static class Task implements Runnable {

        @Override
        public void run() {
            for (int i = 0, n = 10; i < n; i++) {
                int random = ThreadLocalRandom.current().nextInt(n) + 3;
                System.out.println(Thread.currentThread().getName() + " 正在执行任务，随机数：" + random);
                if (random <= n) {
                    throw new RuntimeException("随机数为：" + n + "，抛出异常");
                }
            }
        }
    }
}
