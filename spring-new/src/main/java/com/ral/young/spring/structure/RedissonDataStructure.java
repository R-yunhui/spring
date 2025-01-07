package com.ral.young.spring.structure;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author renyunhui
 * @description Redisson 数据结构示例
 * @date 2024-01-04
 */
@Slf4j
public class RedissonDataStructure {

    /*
     * Redisson 的主要分布式数据结构特点：
     * 分布式锁（Lock）
     *   可重入锁
     *   公平锁
     *   读写锁
     *   联锁（MultiLock）
     *   红锁（RedLock）
     *
     * 布隆过滤器（BloomFilter）
     *   大数据量下的快速判断
     *   可设置误判率
     *   分布式环境下的数据判重
     *
     * 限流器（RateLimiter）
     *   分布式限流
     *   支持不同的限流策略
     *   精确的令牌桶算法实现
     *
     * 分布式集合
     *   Map（支持本地缓存）
     *   Set
     *   List
     *   SortedSet
     *   Queue
     *   Deque
     *
     * 话题（Topic）
     *   发布订阅模式
     *   支持模式订阅
     *   可靠消息投递
     *
     * 延迟队列
     *   支持延迟执行
     *   精确的调度
     *   分布式环境下的任务调度
     */

    private static final RedissonClient redisson;

    static {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://101.43.7.180:6379");
        config.useSingleServer().setDatabase(0);
        config.useSingleServer().setPassword("ryh123.0");
        redisson = Redisson.create(config);
    }

    public static void main(String[] args) throws InterruptedException {
        log.info("===== Redisson 分布式锁示例 =====");
        lockExample();

        log.info("===== Redisson 布隆过滤器示例 =====");
        bloomFilterExample();

        log.info("===== Redisson 限流器示例 =====");
        rateLimiterExample();

        log.info("===== Redisson 分布式集合示例 =====");
        collectionExample();

        log.info("===== Redisson 分布式话题示例 =====");
        topicExample();

        log.info("===== Redisson 分布式延迟队列示例 =====");
        delayedQueueExample();

        redisson.shutdown();
    }

    /**
     * 分布式锁示例
     * 包括：可重入锁、公平锁、读写锁
     */
    public static void lockExample() {
        // 1. 可重入锁
        RLock lock = redisson.getLock("myLock");
        try {
            // 尝试加锁，最多等待100秒，上锁10秒后自动解锁
            boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    log.info("获得锁，执行业务逻辑");
                    // 业务逻辑
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("获取锁被中断", e);
        }

        // 2. 公平锁
        RLock fairLock = redisson.getFairLock("myFairLock");

        // 3. 读写锁
        RReadWriteLock rwLock = redisson.getReadWriteLock("myRWLock");
        RLock readLock = rwLock.readLock();
        RLock writeLock = rwLock.writeLock();
    }

    /**
     * 布隆过滤器示例
     * 用于大数据量的存在性检查
     */
    public static void bloomFilterExample() {
        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("myBloomFilter");
        // 初始化布隆过滤器，预计元素数量为55000，期望误差率为0.03
        bloomFilter.tryInit(55000L, 0.03);

        // 添加元素
        bloomFilter.add("item1");
        bloomFilter.add("item2");

        // 检查元素是否可能存在
        boolean contains = bloomFilter.contains("item1");
        log.info("布隆过滤器是否包含item1: {}", contains);
    }

    /**
     * 限流器示例
     * 用于限制操作频率
     */
    public static void rateLimiterExample() {
        RRateLimiter rateLimiter = redisson.getRateLimiter("myRateLimiter");
        // 初始化：每1秒产生2个令牌
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        // 尝试获取令牌
        boolean acquired = rateLimiter.tryAcquire();
        log.info("是否获取到令牌: {}", acquired);
    }

    /**
     * 分布式集合示例
     * 包括：Set、Map、List等
     */
    public static void collectionExample() {
        // 1. 分布式Set
        RSet<String> set = redisson.getSet("mySet");
        set.add("item1");
        set.add("item2");
        log.info("Set内容: {}", set);

        // 2. 分布式Map
        RMap<String, String> map = redisson.getMap("myMap");
        map.put("key1", "value1");
        map.put("key2", "value2");
        log.info("Map内容: {}", map);

        // 3. 分布式List
        RList<String> list = redisson.getList("myList");
        list.add("item1");
        list.add("item2");
        log.info("List内容: {}", list);

        // 4. 分布式SortedSet
        RSortedSet<String> sortedSet = redisson.getSortedSet("mySortedSet");
        sortedSet.add("item1");
        sortedSet.add("item2");
        log.info("SortedSet内容: {}", sortedSet);
    }

    /**
     * 分布式话题（发布订阅）示例
     */
    public static void topicExample() {
        RTopic topic = redisson.getTopic("myTopic");

        // 添加监听器
        topic.addListener(String.class, (channel, msg) -> {
            log.info("收到消息: {}", msg);
        });

        // 发布消息
        topic.publish("Hello, Redisson!");
    }

    /**
     * 延迟队列示例
     * 用于延迟任务处理
     */
    public static void delayedQueueExample() throws InterruptedException {
        RDelayedQueue<String> delayedQueue = redisson.getDelayedQueue(redisson.getQueue("myQueue"));

        // 添加延迟任务
        delayedQueue.offer("delayed task 1", 5, TimeUnit.SECONDS);
        delayedQueue.offer("delayed task 2", 10, TimeUnit.SECONDS);

        // 监听队列
        RQueue<String> queue = redisson.getQueue("myQueue");
        new Thread(() -> {
            while (true) {
                // 使用 poll 替代 take
                String task = queue.poll();
                if (task != null) {
                    log.info("处理延迟任务: {}", task);
                }

                try {
                    // 添加适当的休眠，避免CPU占用过高
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();

        // 等待一段时间观察结果
        Thread.sleep(15000);

        // 清理资源
        delayedQueue.destroy();
    }
}