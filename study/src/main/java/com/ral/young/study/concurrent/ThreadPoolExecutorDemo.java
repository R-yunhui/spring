package com.ral.young.study.concurrent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author renyunhui
 * @date 2023-12-08 11:19
 * @since 1.0.0
 */
@Slf4j
public class ThreadPoolExecutorDemo {

    public static void main(String[] args) {
        testThree();
    }

    public static void testOne() {
        /*
         * 核心线程数 4
         * 最大线程数 8
         * 线程的最长空闲时间 1分钟
         * 工作队列 容量为 1024 的 ArrayBlockingQueue
         * 线程工厂：指定了线程名称的前缀，线程异常处理
         * 当任务队列满了且达到了最大线程数的丢失策略：直接执行当前的任务
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), new ThreadFactoryBuilder().setNamePrefix("work-").setUncaughtExceptionHandler((t, e) -> System.out.println(Thread.currentThread().getName() + " 执行过程中出现异常,e" + e)).build(), new ThreadPoolExecutor.CallerRunsPolicy());
        AtomicInteger atomicInteger = new AtomicInteger();
        for (int i = 0, size = 100; i < size; i++) {
            atomicInteger.getAndIncrement();
            threadPoolExecutor.execute(() -> {
                int random = ThreadLocalRandom.current().nextInt() + 10;
                atomicInteger.addAndGet(random);
                if (random < 1) {
                    throw new RuntimeException("出现数据异常的情况");
                }
                System.out.println("当前数据:" + atomicInteger.get());
            });
        }
        threadPoolExecutor.shutdown();
    }

    public static void testTwo() {
        ScheduledThreadPoolExecutor scheduleWithFixedDelayPool = new ScheduledThreadPoolExecutor(2, new ThreadFactoryBuilder().setNamePrefix("fixedDelay-").build());
        // 按照固定的延迟去执行，假设第一次执行完成时间为 time ，则第二次执行的时间为 time + delay
        // 具体逻辑见：java.util.concurrent.ScheduledThreadPoolExecutor.ScheduledFutureTask.setNextRunTime
        scheduleWithFixedDelayPool.scheduleWithFixedDelay(() -> {
            log.info(Thread.currentThread().getName() + " 开始执行任务,执行任务开始时间 " + DateUtil.now());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info(Thread.currentThread().getName() + " 任务执行完成,执行任务完成时间 " + DateUtil.now());
        }, 3, 5, TimeUnit.SECONDS);
    }

    public static void testThree() {
        ScheduledThreadPoolExecutor scheduleAtFixedRatePool = new ScheduledThreadPoolExecutor(2, new ThreadFactoryBuilder().setNamePrefix("fixedRate-").build());
        // 安装固定的速率去执行，假设第一次开始执行时间为 time ，则第二次执行的时间为 time + delay
        // 【注】：如果第一次任务执行时间超过了 time + delay ，则会导致第二个任务会在第一个任务执行完成之后立刻执行
        // 具体逻辑见：java.util.concurrent.ScheduledThreadPoolExecutor.ScheduledFutureTask.setNextRunTime
        scheduleAtFixedRatePool.scheduleAtFixedRate(() -> {
            log.info(Thread.currentThread().getName() + " 开始执行任务,执行任务开始时间 " + DateUtil.now());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info(Thread.currentThread().getName() + " 任务执行完成,执行任务完成时间 " + DateUtil.now());
        }, 3, 5, TimeUnit.SECONDS);
    }
}
