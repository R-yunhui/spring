package com.ral.young.basic.concurrent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.ScheduledThreadPoolExecutor}
 *
 * @author renyunhui
 * @date 2023-05-31 13:29
 * @since 1.0.0
 */
public class ScheduledThreadPoolExecutorDemo {

    public static void main(String[] args) throws InterruptedException {
        ScheduledThreadPoolExecutorDemo demo = new ScheduledThreadPoolExecutorDemo();
        // 测试固定周期执行定时任务
        // demo.testFixedRate();

        // 测试固定延迟执行定时任务
        // demo.testFixedDelay();

        demo.testSchedule();
    }

    private void testFixedRate() throws InterruptedException {
        // 使用 ScheduledThreadPoolExecutor 实现固定周期执行某一个任务
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, ThreadFactoryBuilder.create().setNamePrefix("test-fixedRate-").build());

        // 1s 后开始执行任务，之后每 2s 执行一次任务，任务下一次执行的时间 = 上一次任务开始执行的时间 + 设置的周期时间
        executor.scheduleAtFixedRate(() -> {
            System.out.println(Thread.currentThread().getName() + "：开始执行定时周期任务,当前时间：" + DateUtil.now());
            // 模拟任务执行时间超过了设置的延迟时间，则下一次任务会在本次任务执行完毕之后立刻执行
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);


        Thread.sleep(15000);
        executor.shutdown();
    }

    private void testFixedDelay() throws InterruptedException {
        // 使用 ScheduledThreadPoolExecutor 实现固定周期执行某一个任务

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, ThreadFactoryBuilder.create().setNamePrefix("test-testFixRate-").build());

        // 1s 后开始执行任务，之后每 2s 执行一次任务，任务下一次执行的时间 = 上一个任务完成的时间 + 设置的延迟时间
        executor.scheduleWithFixedDelay(() -> {
            System.out.println(Thread.currentThread().getName() + "：开始执行定时延迟任务,当前时间：" + DateUtil.now());
            // 模拟任务执行时间超过了设置的延迟时间，则下一次任务会在本次任务执行完毕之后等待设置的延迟时间之后立刻执行
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + "：定时延迟任务执行完毕,当前时间：" + DateUtil.now());
        }, 1000, 2000, TimeUnit.MILLISECONDS);


        Thread.sleep(15000);
        executor.shutdown();
    }

    private void testSchedule() throws InterruptedException {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, ThreadFactoryBuilder.create().setNamePrefix("test-schedule-").build());

        // 这种方式不属于周期性执行的任务，需要手动向线程池中添加任务才会触发任务的执行
        executor.schedule(() -> {
            System.out.println(Thread.currentThread().getName() + "：开始执行定时任务,当前时间：" + DateUtil.now());

            // 如果后续不在添加新的任务则不会再执行任务，必须手动向线程池中添加新的任务才可以
            executor.schedule(() -> {
                System.out.println(Thread.currentThread().getName() + "：开始执行定时任务,当前时间：" + DateUtil.now());

            }, 1000, TimeUnit.MILLISECONDS);

        }, 1000, TimeUnit.MILLISECONDS);


        Thread.sleep(10000);
        executor.shutdown();
    }
}
