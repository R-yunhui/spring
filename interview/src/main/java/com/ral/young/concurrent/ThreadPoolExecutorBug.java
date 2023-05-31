package com.ral.young.concurrent;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import net.openhft.affinity.AffinityLock;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池错误设置导致的 cpu 飙升问题
 *
 * @author renyunhui
 * @date 2023-02-15 11:15
 * @since 1.0.0
 */
public class ThreadPoolExecutorBug {

    public static void main(String[] args) {
        // testThreadPoolBug();
        simulateBug();
    }

    public static void testThreadPoolBug() {
        // 根本原因：corePoolSize = 0  keepAlive = 0，导致从队列中获取不到任务的时候，会一直在循环体内尝试获取任务，阻塞时间 = 0，会一直空循环
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(0, ThreadFactoryBuilder.create().setNamePrefix("bug-thread-%d").build());
        for (;;) {
            scheduledThreadPoolExecutor.schedule(() -> {
                System.out.println("bug test");
            }, 6, TimeUnit.SECONDS);
        }
    }

    public static void simulateBug() {
        boolean timed = true;
        long keepAliveTime = 0L;
        ArrayBlockingQueue<Runnable> workQueue =
                new ArrayBlockingQueue<>(100);
        // 绑定到 5 号 CPU 上执行，模拟上述线程池 bug 的根本原因
        try (AffinityLock ignored1 = AffinityLock.acquireLock(5)) {
            for (; ; ) {
                try {
                    Runnable r = timed ?
                            workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                            workQueue.take();
                    if (r != null) {
                        break;
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
