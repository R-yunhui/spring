package com.ral.young.concurrent.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * {@link org.apache.tomcat.util.threads.ThreadPoolExecutor}
 *
 * @author renyunhui
 * @date 2022-11-04 10:10
 * @since 1.0.0
 */
@Slf4j
public class ThreadPoolExecutorTest {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

    private static final ArrayBlockingQueue<Thread> BLOCKING_QUEUE = new ArrayBlockingQueue<>(1024);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0, size = 10; i < size; i++) {
            int finalI = i;
            THREAD_POOL_EXECUTOR.execute(() -> {
                log.info("{},执行第{}个任务", Thread.currentThread().getName(), finalI);
                BLOCKING_QUEUE.add(Thread.currentThread());
                // 抛出异常之后，会调用 addWorker 添加新的 Worker 执行后续的任务
                throw new RuntimeException("异常");
            });
        }

        Thread.sleep(2000);
        Thread t1 = BLOCKING_QUEUE.poll();
        while (!BLOCKING_QUEUE.isEmpty()) {
            Thread t2 = BLOCKING_QUEUE.poll();
            log.info("线程是否一致:{}", t1 == t2);
            t1 = t2;
        }

        THREAD_POOL_EXECUTOR.shutdown();
    }
}
