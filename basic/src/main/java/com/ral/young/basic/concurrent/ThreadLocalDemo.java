package com.ral.young.basic.concurrent;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.IdUtil;
import org.springframework.util.StringUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 模拟线程池中使用 ThreadLocal 的问题
 *
 * @author renyunhui
 * @date 2023-06-02 16:08
 * @since 1.0.0
 */
public class ThreadLocalDemo {


    private static final ThreadLocal<String> STRING_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1024), ThreadFactoryBuilder.create().setNamePrefix("worker-").build());

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        // 使用 CountDownLatch 来模拟并发
        for (int i = 0, n = 20; i < n; i++) {
            EXECUTOR.execute(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String before = STRING_THREAD_LOCAL.get();
                // 由于存在线程复用，导致后续同一个线程被复用获取到的 ThreadLocal 中的数据是一致的
                System.out.println(Thread.currentThread().getName() + " before ：" + before);

                if (StringUtils.isEmpty(before)) {
                    String after = IdUtil.fastSimpleUUID();
                    STRING_THREAD_LOCAL.set(after);
                    System.out.println(Thread.currentThread().getName() + " after ：" + after);
                }
            });
        }

        Thread.sleep(1000);
        countDownLatch.countDown();

        EXECUTOR.shutdown();
    }
}
