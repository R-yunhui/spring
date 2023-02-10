package com.ral.young.concurrent;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.Semaphore}
 *
 * @author renyunhui
 * @date 2023-02-08 15:01
 * @since 1.0.0
 */
@Slf4j
public class SemaphoreTest {

    static Semaphore semaphore = new Semaphore(3);

    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), ThreadFactoryBuilder.create().setNameFormat("work-thread-%d").get());

    public static void main(String[] args) {
        for (int i = 0, n = 10; i < n; i++) {
            threadPoolExecutor.execute(new Task());
        }

        threadPoolExecutor.shutdown();
    }

    public static class Task implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            // 允许中断的方式获取许可
            semaphore.acquire();
            log.info("{}:获取许可证完成", Thread.currentThread().getName());

            TimeUnit.SECONDS.sleep(10);

            semaphore.release();
            log.info("任务执行完毕,{}:释放许可证完成", Thread.currentThread().getName());
        }
    }
}
