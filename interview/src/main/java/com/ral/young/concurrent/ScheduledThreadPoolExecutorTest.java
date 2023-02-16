package com.ral.young.concurrent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.ScheduledThreadPoolExecutor}
 *
 * @author renyunhui
 * @date 2023-02-15 13:41
 * @since 1.0.0
 */
@Slf4j
public class ScheduledThreadPoolExecutorTest {

    public static void main(String[] args) throws InterruptedException {
        testScheduledWithFixedDelay();
        // testScheduledWithFixedRate();
    }

    private static void testScheduledWithFixedDelay() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, ThreadFactoryBuilder.create().setNamePrefix("testScheduledWithFixedDelay-").build());

        // 以固定的延迟时间执行任务，下一次执行任务的时间是本次执行任务完成后的时间 now() + 设置的固定延迟时间
        // 下一次任务的执行一定是在本次任务执行完成之后的固定延期时间执行
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> {
            log.info("任务执行中,执行任务的线程名称:{},当前线程池的运行的线程数:{},时间:{}", Thread.currentThread().getName(), scheduledThreadPoolExecutor.getPoolSize(), DateUtil.now());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("任务执行完毕,执行任务的线程名称:{},时间:{}", Thread.currentThread().getName(), DateUtil.now());
        }, 0, 1, TimeUnit.SECONDS);
    }


    private static void testScheduledWithFixedRate() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(3, ThreadFactoryBuilder.create().setNamePrefix("testScheduledWithFixedRate-").build());

        // 以固定的速率执行任务，下一次执行任务的时间是初始化线程池的时候的 now() + 设置的延迟时间
        // 如果本次任务执行时间太长，可能导致由别的线程执行下一次的任务，不会等待当前任务完成再去执行；如果只有一个线程那么可能存在执行完当前任务之后，还不到配置的延期时间就开始执行任务了
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            log.info("任务执行中,执行任务的线程名称:{},当前线程池的运行的线程数:{},时间:{}", Thread.currentThread().getName(), scheduledThreadPoolExecutor.getPoolSize(), DateUtil.now());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("任务执行完毕,执行任务的线程名称:{},时间:{}", Thread.currentThread().getName(), DateUtil.now());
        }, 0, 1, TimeUnit.SECONDS);
    }
}
