package com.ral.young.night.redis;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.ral.young.night.redis.config.RedisConfig;
import com.ral.young.night.redis.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * @author renyunhui
 * @date 2024-06-20 14:09
 * @since 1.0.0
 */
@Slf4j
public class RedisApplication {

    private static final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    private static final List<String> userIdList = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        applicationContext.register(RedisConfig.class);
        applicationContext.refresh();

        /*
         * redis 和 数据库的双写一致性
         * 1.先删除 redis 数据，在更新数据库，延迟一定时间在删除 redis 数据
         * 2.先删除缓存，再更新数据库，发送一个异步消息或者通过监听 mysql 的 binlog 再去清除缓存
         *
         * 除了缓存以外的使用场景：
         * 1.消息队列（不建议）
         * 2.延迟消息（不建议  zset or redission 的 RDelayQueue）
         * 3.排行榜 set
         * 4.计数器 incr
         * 5.分布式ID
         * 6.地理位置 geo
         * 7.分布式锁 setnx or lua 脚本
         * 8.分布式限流
         * 9.分布式 session
         * 10.布隆过滤器（解决缓存穿透 or 大量用户的状态统计）
         * 11.共同关注 set 交集，并集，差集
         * 12.推荐关注 同 11
         */
        // 模拟排行榜
        // testRankings();

        // 模拟共同关注（交集 并集 差集）
        // testCommonFollow();

        // 测试布隆过滤器
        // initUserInfo();
        // testBloomFilter();

        TestService bean = applicationContext.getBean(TestService.class);
        bean.testDelayQueue();

        Thread.sleep(100000000);
        applicationContext.close();
    }

    public static void testRedissonLock() {
        /*
         * redisson 实现分布式锁：
         * 1.自动续租：或取到分布式锁之后，WatchDog 会通过 netty 的时间轮启动一个后台任务，定期向 redis 发送命令，重置锁的过期时间，通常是锁的 leaseTime 的 1/3
         * 2.续期时长：默认 没 10s 做一次续期，默认续期 30s
         * 3.停止续费：当锁被释放或者客户端实例关系，WatchDog 会自动停止续期的任务
         */
        RedissonClient redissonClient = applicationContext.getBean(RedissonClient.class);
        String lockKey = "myLock";
        // redisson 的分布式锁是个 hash 结构，lockKey 即是整个锁的 key
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 加锁的 hash 结构中的 hashtable 中的 entry 的 key：uuid:ThreadId  主要是为了防止其它线程释放了不属于自己线程的锁
            // 不加超时时间，默认 30000 ms
            lock.lock();
            log.info("{} 或取到了分布式锁，开始执行任务", Thread.currentThread().getId());
            Thread.sleep(1000000);
            log.info("{} 执行完成，准备释放分布式锁", Thread.currentThread().getId());
        } catch (Exception e) {
            log.error("执行异常，e：", e);
        } finally {
            lock.unlock();
        }
    }

    public static void testRankings() throws InterruptedException {
        // 使用 redis 实现 排行榜
        StringRedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        String key = "rankings";
        for (int i = 0, n = 10; i < n; i++) {
            // 通过 10 个线程进行模拟自增分数，然后获取 top 3
            new Thread(() -> {
                int score = ThreadLocalRandom.current().nextInt(10);
                for (int j = 0; j < 10; j++) {
                    redisTemplate.opsForZSet().incrementScore(key, Thread.currentThread().getName(), score);
                }
            }).start();
        }

        Thread.sleep(3000);
        // 主线程获取 top3  从大到小 api：zrevrange  从小大大：zrange
        Set<String> results = redisTemplate.opsForZSet().reverseRange(key, 0, 2);
        log.info("top3：{}", results);
    }

    public static void testCommonFollow() {
        StringRedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        String userOne = "user::one";
        String userTwo = "user::two";
        String userThree = "user::three";

        // 模拟关注
        redisTemplate.opsForSet().add(userOne, "1", "2", "3", "4", "5");
        redisTemplate.opsForSet().add(userTwo, "11", "22", "3", "4", "5");
        redisTemplate.opsForSet().add(userThree, "111", "2", "333", "444", "5");

        // 获取 userOne and userTwo 的共同关注
        Set<String> intersect = redisTemplate.opsForSet().intersect(userOne, userTwo);
        log.info("userOne and userTwo 的共同关注：{}", intersect);

        Set<String> union = redisTemplate.opsForSet().union(Lists.newArrayList(userOne, userTwo, userThree));
        log.info("userOne and userTwo and userThree 的所有关注：{}", union);

        Set<String> difference = redisTemplate.opsForSet().difference(userOne, userTwo);
        log.info("userOne and userTwo 的非共同关注：{}", difference);
    }

    public static void testBloomFilter() {
        RedissonClient redissonClient = applicationContext.getBean(RedissonClient.class);
        /*
         * 布隆过滤器：本质是一个位数组，默认是 64 bit，可以设置位数，错误率，初始化元素数量，初始化之后，可以动态添加元素，但是不能动态减少元素
         * 通过几个 hash 算法计算出一个位置，然后设置位为 1，如果位置为 1，则表示该元素存在，如果位置为 0，则表示该元素不存在
         * 一般来说，错误率越低，误判率越低，但是空间占用变大
         *
         * 布隆过滤器里面存在，但是数据库可能不存在，布隆过滤器不存在，数据库一定存在
         */
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("test::bloomFilter");
        // 初始化 bloomFilter  预期元素插入量：100000  错误率：0.03
        bloomFilter.tryInit(100000, 0.03);
        // 初始化 100000 数据
        userIdList.forEach(bloomFilter::add);
        log.info("bloomFilter count：{}", bloomFilter.count());

        // 随机使用几个元素看看 bloomFilter 是否存在
        for (int i = 0; i < 10; i++) {
            String userId = userIdList.get(ThreadLocalRandom.current().nextInt(userIdList.size()));
            log.info("one bloomFilter contains {}：{}", userId, bloomFilter.contains(userId));
        }

        // 在使用几个不存在的元素看看 bloomFilter 是否存在
        for (int i = 0; i < 10; i++) {
            String userId = IdUtil.getSnowflakeNextIdStr();
            boolean contains = bloomFilter.contains(userId);
            if (contains) {
                log.info("two bloomFilter contains {}", userId);
                log.info("two userIdList contains {}：{}", userId, userIdList.contains(userId));
            }
        }
    }

    public static void initUserInfo() {
        IntStream.range(0, 100000).forEach(i -> userIdList.add(IdUtil.getSnowflakeNextIdStr()));
    }
}
