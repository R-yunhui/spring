package com.ral.young.concurrent.forkjoin;

import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 适用 ForkJoin 框架求和大数
 * {@link java.util.concurrent.ForkJoinPool}
 *
 * @author renyunhui
 * @date 2023-02-14 10:07
 * @since 1.0.0
 */
@Slf4j
public class LongSumMain {

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int LOW = 1;

    private static final int HIGH = 1000 * 1000 * 1000;

    private static int[] array;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        initArray();
        StopWatch stopWatch = new StopWatch();
        // 单线程进行模拟
        stopWatch.start("单线程执行大数求和");
        long seqSum = 0;
        for (int j : array) {
            seqSum += j;
        }
        log.info("单线程执行大数求和结果:{}", seqSum);
        stopWatch.stop();

        stopWatch.start("fork join 执行大数求和");
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        LongSumTask longSumTask = new LongSumTask(0, array.length, array);
        ForkJoinTask<Long> task = forkJoinPool.submit(longSumTask);
        Long sum = task.get();
        log.info("多线程执行大数求和结果:{}", sum);
        stopWatch.stop();
        log.info("耗时统计:{}", stopWatch.prettyPrint());

        forkJoinPool.shutdownNow();
    }

    private static void initArray() {
        array = new int[HIGH - LOW + 1];
        for (int i = LOW; i < HIGH; i++) {
            array[i] = i;
        }
    }
}
