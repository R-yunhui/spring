package com.ral.young.night.concurrent;

import cn.hutool.core.util.IdUtil;
import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 需求：
 * 多线程操作一批数据等待所有任务执行完成之后。再开始执行后续的操作
 *
 * @author renyunhui
 * @date 2024-07-16 11:56
 * @since 1.0.0
 */
@Slf4j
public class ThreadPoolDemo {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 10, 60
            , TimeUnit.SECONDS, new LinkedBlockingQueue<>(100)
            , ThreadFactoryBuilder.create().setNameFormat("work-%d").get()
            , new ThreadPoolExecutor.AbortPolicy());

    static {
        // 线程池预热，初始化创建线程池里面的核心线程
        executor.prestartAllCoreThreads();
    }

    public static void main(String[] args) throws InterruptedException {
        /*
         * Executors
         * 1.Executors.newFixedThreadPool() 创建一个固定大小的线程池
         * 2.Executors.newSingleThreadExecutor() 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
         * 1 和 2 都是任务队列是一个 LinkedBlockingQueue 大小为 Integer.MAX_VALUE 可能导致 OOM
         *
         * 3.Executors.newCachedThreadPool() 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
         * 4.Executors.newScheduledThreadPool() 创建一个定长线程池，支持定时及周期性任务执行。
         * 3 和 4 都是最大线程数为 Integer.MAX_VALUE，可能导致 OOM
         */


        // 模拟存放任务的队列
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(30);
        IntStream.range(0, 30).forEach(i -> arrayBlockingQueue.add(IdUtil.fastSimpleUUID()));
        CountDownLatch countDownLatch = new CountDownLatch(arrayBlockingQueue.size());
        IntStream.range(0, 30).forEach(i -> executor.execute(() -> {
            try {
                log.info("{}: {}", Thread.currentThread().getName(), arrayBlockingQueue.take());
                Thread.sleep(500);
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                log.error("线程中断异常：", e);
                Thread.currentThread().interrupt();
            }
        }));

        countDownLatch.await();
        log.info("子线程完成了所有的任务，主线程开始执行任务");

        // 关闭线程池
        executor.shutdown();
    }
}
