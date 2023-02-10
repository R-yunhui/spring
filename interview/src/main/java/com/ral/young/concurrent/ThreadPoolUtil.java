package com.ral.young.concurrent;

import cn.hutool.core.date.DateUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的工具类
 *
 * @author renyunhui
 * @date 2023-02-09 16:54
 * @since 1.0.0
 */
@Slf4j
public class ThreadPoolUtil {

    static String defaultThreadPoolName = "work";

    static ThreadFactory defaultThreadFactory;

    static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    static Thread.UncaughtExceptionHandler defaultExceptionHandler;

    static {
        // 设置默认的线程工程
        defaultExceptionHandler = new DefaultThreadPoolExceptionHandler();
        defaultThreadFactory = new ThreadFactoryBuilder().setNameFormat(defaultThreadPoolName + "-pool-%d").setUncaughtExceptionHandler(defaultExceptionHandler).build();

        // 创建定时任务线程池监听用此类创建的线程池的工作状态
        ThreadFactory defaultThreadFactory = new ThreadFactoryBuilder().setNameFormat("monitor-pool-%d").setUncaughtExceptionHandler(defaultExceptionHandler).build();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, defaultThreadFactory);
        // 开启监听任务
        monitorThreadPool();
    }

    static ConcurrentHashMap<String, ThreadPoolExecutor> concurrentHashMap = new ConcurrentHashMap<>();

    public static ThreadPoolExecutor createPoolExecutor(int coreSize, int maxSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> blockingQueue, boolean monitored) {
        // 可以进一步完善：指定线程池的名字，暂定线程名为线程池的名字
        ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, unit, blockingQueue, defaultThreadFactory);
        saveThreadPoolExecutor(executor, defaultThreadPoolName, monitored);
        return executor;
    }

    public static ThreadPoolExecutor createPoolExecutor(int coreSize, int maxSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> blockingQueue, String threadName, boolean monitored) {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(threadName + "-pool-%d").build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, unit, blockingQueue, factory);
        saveThreadPoolExecutor(executor, threadName, monitored);
        return executor;
    }

    public static ThreadPoolExecutor createPoolExecutor(int coreSize, int maxSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> blockingQueue, String threadName, RejectedExecutionHandler rejectedExecutionHandler, boolean monitored) {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(threadName + "-pool-%d").build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, unit, blockingQueue, factory, rejectedExecutionHandler);
        saveThreadPoolExecutor(executor, threadName, monitored);
        return executor;
    }

    private static void saveThreadPoolExecutor(ThreadPoolExecutor executor, String threadName, boolean monitored) {
        if (monitored) {
            concurrentHashMap.put(threadName, executor);
        }
    }

    private static void monitorThreadPool() {
        // 每分钟执行一次监听任务
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            log.info("开始监听程序中创建的线程池的状态,时间:{}", DateUtil.now());
            if (concurrentHashMap.isEmpty()) {
                log.info("当前未存在需要监听的线程池");
                return;
            }

            concurrentHashMap.forEach((k, v) -> {
                log.info("===== 线程池:{} =====", k);
                log.info("配置的核心线程数量:{}", v.getCorePoolSize());
                log.info("配置的最大线程数:{}", v.getMaximumPoolSize());
                log.info("配置的线程空闲时间:{}ms", v.getKeepAliveTime(TimeUnit.MICROSECONDS));
                log.info("配置的拒绝策略:{}", v.getRejectedExecutionHandler().getClass().getName());
                log.info("配置的工作队列:{}", v.getQueue().getClass().getName());
                log.info("是否允许核心线程被回收:{}\n", v.allowsCoreThreadTimeOut());

                // 会使用ThreadPoolExecutor 中的 ReentrantLock 进行加锁保证并发安全
                log.info("正在执行任务的工作线程数量:{}", v.getActiveCount());
                log.info("线程池中目前同时存在的最大线程数:{}", v.getLargestPoolSize());
                log.info("当前线程池中存在的工作线程数量:{}", v.getPoolSize());
                log.info("目前线程池工作队列中积压的任务数量:{}", v.getQueue().size());
                log.info("线程池目前完成的任务数量:{}", v.getCompletedTaskCount());
                log.info("目前线程池接收到的任务总数(包含已经销毁的线程执行的数量):{}\n\n", v.getTaskCount());
            });
        }, 5, 60, TimeUnit.SECONDS);
    }
}
