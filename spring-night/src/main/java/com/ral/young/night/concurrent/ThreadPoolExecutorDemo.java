package com.ral.young.night.concurrent;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池Demo
 *
 * @author renyunhui
 * @date 2024-05-28 10:53
 * @since 1.0.0
 */
public class ThreadPoolExecutorDemo {

    public static void main(String[] args) {
        test();
    }

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 3, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10),
            // 设置自定义线程异常处理器
            ThreadFactoryBuilder.create().setNamePrefix("test-thread-").setUncaughtExceptionHandler(new CustomExceptionHandler()).build(),
            // 设置自定义线程池任务拒绝策略
            new CustomRejectedExecutionHandler());

    public static void test() {
        for (int i = 0, n = 100; i < n; i++) {
            executor.execute(() -> {
                sayThreadPoolStatus();
                System.out.println(Thread.currentThread().getName() + "执行任务");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("线程中断异常");
                }
                System.out.println(Thread.currentThread().getName() + "任务执行完毕");
            });
        }
    }

    private static void sayThreadPoolStatus() {
        System.out.println("===== 线程池状态监控 =====");
        System.out.println("配置的核心线程数：" + executor.getCorePoolSize());
        System.out.println("配置的最大线程数：" + executor.getMaximumPoolSize());
        System.out.println("当前线程数：" + executor.getPoolSize());
        System.out.println("当前正在运行线程数：" + executor.getActiveCount());
        System.out.println("运行存在的最大线程数：" + executor.getLargestPoolSize());
        System.out.println("配置的拒绝策略：" + executor.getRejectedExecutionHandler().getClass().getName());
        System.out.println("配置的任务队列：" + executor.getQueue().getClass().getName());
        System.out.println("是否允许核心线程被回收：" + executor.allowsCoreThreadTimeOut());
        System.out.println("当前任务队列任务数量：" + executor.getQueue().size());
        System.out.println("当前任务队列剩余可容纳任务数量：" + executor.getQueue().remainingCapacity());
        System.out.println("当前任务队列已用容量：" + (executor.getQueue().size() - executor.getQueue().remainingCapacity()));
        System.out.println("当前线程池包含的任务总量：" + executor.getTaskCount());
        System.out.println("当前线程池已完成任务数量：" + executor.getCompletedTaskCount());
        System.out.println("===== 线程池状态监控 =====");
    }
}
