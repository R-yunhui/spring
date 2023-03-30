package com.ral.young.bug.concurrent;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * {@link java.util.concurrent.ConcurrentHashMap}
 *
 * @author renyunhui
 * @date 2023-03-08 16:24
 * @since 1.0.0
 */
@Slf4j
public class ConcurrentHashMapError {

    static int size = 1000;

    static int threadCount = 10;

    static int loopCount = 10000000;

    public static void main(String[] args) throws InterruptedException {
        testTwo();
    }

    public static void testOne() throws InterruptedException {
        ConcurrentHashMap<String, Long> concurrentHashMap = initData(size - 100);
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            // 可以通过加锁解决超出容量的问题
            // concurrentHashMap 可以保证 putAll 操作的原子性，但是不能多个操作之间的状态是一致的
            // 可以通过加锁解决 synchronized or lock
            int gap = size - concurrentHashMap.size();
            log.info("需要补充的元素数量:{}", gap);
            concurrentHashMap.putAll(initData(gap));
        }));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.MINUTES);
        log.info("当前 concurrentHashMap 的大小:{}", concurrentHashMap.size());
    }

    public static void testTwo() throws InterruptedException {
        // 需求：如果 map 中存在某个 key 则 + 1，反之就是 1
        ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap<>(size);
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, loopCount).parallel().forEach(i -> {
            String key = "key" + ThreadLocalRandom.current().nextInt(size);
            // 存在并发问题
            // 1.可以加锁解决
            // 2.可以使用 concurrentHashMap 的原子操作解决，性能更高  concurrentHashMap.computeIfAbsent
            if (concurrentHashMap.containsKey(key)) {
                concurrentHashMap.put(key, concurrentHashMap.get(key) + 1);
            } else {
                concurrentHashMap.put(key, 1L);
            }
        }));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.MINUTES);
        log.info("concurrentHashMap 数量:{}", concurrentHashMap.size());
    }

    private static ConcurrentHashMap<String, Long> initData(int count) {
        return LongStream.rangeClosed(1, count).boxed().collect(Collectors.toConcurrentMap(i -> IdUtil.randomUUID(), o -> o, (o1, o2) -> o1, ConcurrentHashMap::new));
    }
}
