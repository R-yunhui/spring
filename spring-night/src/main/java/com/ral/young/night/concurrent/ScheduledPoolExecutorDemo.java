package com.ral.young.night.concurrent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务线程池
 *
 * @author renyunhui
 * @date 2024-05-28 11:07
 * @since 1.0.0
 */
public class ScheduledPoolExecutorDemo {

    public static void main(String[] args) {
        // testOne();

        testTwo();

        // testThree();
    }

    public static void testOne() {
        /*
         *  封装了一个内部类：ScheduledFutureTask  即具体需要执行的任务
         *  最大线程数为 Integer.MAX_VALUE   任务队列为 DelayQueue 延迟队列
         */
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2,
                // 设置自定义线程异常处理器
                ThreadFactoryBuilder.create().setNamePrefix("test-thread-").setUncaughtExceptionHandler(new CustomExceptionHandler()).build(),
                // 设置自定义线程池任务拒绝策略
                new CustomRejectedExecutionHandler());

        for (int i = 0, n = 10; i < n; i++) {
            executor.schedule(new Task(), 1000, TimeUnit.MILLISECONDS);
        }

        executor.shutdown();
    }

    public static void testTwo() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1,
                // 设置自定义线程异常处理器
                ThreadFactoryBuilder.create().setNamePrefix("test-thread-").setUncaughtExceptionHandler(new CustomExceptionHandler()).build(),
                // 设置自定义线程池任务拒绝策略
                new CustomRejectedExecutionHandler());

        // 按照固定周期去执行
        // 例如第一个任务执行超过了这个周期时间，那么下一个任务会在这个任务执行完成之后立刻开始执行，而不是在等待一个固定的周期
        executor.scheduleAtFixedRate(new Task(), 2000, 500, TimeUnit.MILLISECONDS);
    }

    public static void testThree() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1,
                // 设置自定义线程异常处理器
                ThreadFactoryBuilder.create().setNamePrefix("test-thread-").setUncaughtExceptionHandler(new CustomExceptionHandler()).build(),
                // 设置自定义线程池任务拒绝策略
                new CustomRejectedExecutionHandler());

        // 按照固定延迟去执行
        // 例如第一个任务执行超过了这个延迟时间，那么下一个任务会在等待一个固定的延迟时间再去执行
        executor.scheduleWithFixedDelay(new Task(), 2000, 5000, TimeUnit.MILLISECONDS);
    }

    private static class Task implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " 开始执行任务，当前时间：" + DateUtil.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " 任务执行完毕，当前时间：" + DateUtil.now());
        }
    }
}
