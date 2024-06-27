package com.ral.young.night.concurrent;

import cn.hutool.core.util.IdUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal
 * inheritableThreadLocal
 * TransmittableThreadLocal
 *
 * @author renyunhui
 * @date 2024-06-27 15:03
 * @since 1.0.0
 */
public class ThreadLocalDemoTwo {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    private static final InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

    private static final TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();

    public static void main(String[] args) {
        // testThreadLocal();

        // testInheritableThreadLocal();

        testTransmittableThreadLocal();
    }

    public static void testThreadLocal() {
        try {
            String id = IdUtil.randomUUID();
            System.out.println("主线程放入的数据：" + id);
            threadLocal.set(id);

            new Thread(() -> {
                System.out.println("子线程获取到的数据：" + threadLocal.get());
            }).start();

            System.out.println("主线程获取到的数据：" + threadLocal.get());
        } finally {
            threadLocal.remove();
        }
    }

    public static void testInheritableThreadLocal() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));
        String id = IdUtil.randomUUID();
        try {
            System.out.println("主线程放入的数据：" + id);
            inheritableThreadLocal.set(id);

            new Thread(() -> {
                System.out.println("手动创建子线程获取到的数据：" + inheritableThreadLocal.get());
            }).start();

            for (int i = 0; i < 10; i++) {
                executor.execute(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("线程池创建的子线程获取到的数据：" + inheritableThreadLocal.get());
                });
            }

            System.out.println("主线程获取到的数据：" + inheritableThreadLocal.get());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            inheritableThreadLocal.remove();
            executor.shutdown();
        }
    }

    public static void testTransmittableThreadLocal() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));
        // 用 TtlExecutors 去封装我们自定义的线程池，兼容 TransmittableThreadLocal
        Executor ttlExecutor = TtlExecutors.getTtlExecutor(executor);
        String id = IdUtil.randomUUID();
        try {
            System.out.println("主线程放入的数据：" + id);
            transmittableThreadLocal.set(id);

            new Thread(() -> {
                System.out.println("手动创建子线程获取到的数据：" + transmittableThreadLocal.get());
            }).start();

            for (int i = 0; i < 10; i++) {
                ttlExecutor.execute(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("线程池创建的子线程获取到的数据：" + transmittableThreadLocal.get());
                });
            }

            System.out.println("主线程获取到的数据：" + transmittableThreadLocal.get());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            transmittableThreadLocal.remove();
            executor.shutdown();
        }
    }
}
