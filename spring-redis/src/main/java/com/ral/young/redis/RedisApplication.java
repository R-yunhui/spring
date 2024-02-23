package com.ral.young.redis;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.ral.young.redis.config.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 * spring-redis 启动类
 *
 * @author renyunhui
 * @date 2024-01-02 10:16
 * @since 1.0.0
 */
@Slf4j
public class RedisApplication {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(RedisConfig.class);
        applicationContext.refresh();

        StringRedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        // 测试并发向 redis 加锁
        testRedisLock(redisTemplate);

        // 测试 redis 实现延迟队列
        testDelayQueue(redisTemplate);

        // 测试 redis 中 set 的操作
        testSetOperation(redisTemplate);

        // 关闭容器
        applicationContext.close();
    }

    private static void testRedisLock(StringRedisTemplate redisTemplate) throws InterruptedException {
        /*
         * redis 五大基础数据结构：
         * 1.字符串
         * 2.Hash
         * 3.List
         * 4.Set
         * 5.ZSet
         */
        String key = IdUtil.fastSimpleUUID();

        // 多线程模拟获取锁
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0, size = 10; i < size; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    // 手动并发
                    String value = String.valueOf(Thread.currentThread().getId());
                    Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value);
                    log.info("{} 子线程开始执行任务,首先尝试获取锁", Thread.currentThread().getName());
                    if (Boolean.TRUE.equals(success)) {
                        log.info("{} 尝试获取锁成功,key:{},开始执行任务", Thread.currentThread().getName(), key);
                        Thread.sleep(2000);
                        // 释放锁，判断是不是自己持有的锁，优化为 lua 脚本，此操作非原子性
                        String curValue = redisTemplate.opsForValue().get(key);
                        if (value.equals(curValue)) {
                            // 同一把锁则进行释放
                            redisTemplate.delete(key);
                            log.info("{} 释放锁成功,其它线程可以进行锁的竞争", Thread.currentThread().getName());
                        }
                    } else {
                        log.info("{} 尝试获取锁失败", Thread.currentThread().getName());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        Thread.sleep(2000);
        countDownLatch.countDown();
        log.info("主线程释放锁,允许子线程开始执行任务");
    }

    private static void testDelayQueue(StringRedisTemplate redisTemplate) {
        /*
         * 使用 redis 实现延迟队列
         * 1.使用 sorted set
         * 2.入队的命令：zadd key score value  -> zadd delay::queue 1708569383686 r1  使用时间戳作为 score 的值
         * 3.获取延迟队列数据的命令：zrangebyscore key min_score max_score 获取数据即可
         */
        long time = DateUtil.date().getTime();
        // 每个2s 输出一次
        String key = "delay:queue";
        IntStream.range(1, 10).forEach(i -> {
            long a = 1000L * 2 * i;
            long b = time - a;
            System.out.println(b);

            redisTemplate.opsForZSet().add(key, "ryh" + i, b);
        });

        // 获取制定时间范围内的数据，例如获取 3s 前的数据
        long minScore = time - (1000L * 3);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().rangeByScoreWithScores(key, minScore, time);
        assert typedTuples != null;
        typedTuples.forEach(o -> {
            System.out.println(o.getScore() + "  " + o.getValue());
        });
    }

    private static void testSetOperation(StringRedisTemplate redisTemplate) {
        /*
         * 使用 redis 实现抽奖功能
         * 1.使用 set
         * 2.通过命令：sadd key value[....]
         * 3.通过命令获取：srandmember key count
         */
        // 初始化一批人员 id
        List<String> userIdList = new ArrayList<>();
        IntStream.range(0, 20).forEach(i -> userIdList.add(IdUtil.fastSimpleUUID()));
        // 模拟存储用户id 到redis 的 set 集合中
        String key = "test::lottery";
        redisTemplate.opsForSet().add(key, String.join(" ", userIdList));

        // 随机抽取3个用户
        List<String> winningUserIds = redisTemplate.opsForSet().randomMembers(key, 3);
        log.info("中奖的用户信息：{}", winningUserIds);

        /*
         * 使用 redis 实现集合的交集，并集，差集的操作
         * 1.并集 sunion
         * 2.差集 sdiff
         * 3.交集 sinter
         */
        // 使用两个集合进行测试
        List<String> listOne = new ArrayList<>();
        List<String> listTwo = new ArrayList<>();
        IntStream.range(1, 20).forEach(i -> listOne.add(String.valueOf(i)));
        IntStream.range(11, 20).forEach(i -> listTwo.add(String.valueOf(i)));

        // 存储redis
        String key1 = "test::listOne";
        String key2 = "test::listTwo";
        redisTemplate.opsForSet().add(key1, String.join(" ", listOne));
        redisTemplate.opsForSet().add(key2, String.join(" ", listTwo));

        // 交集
        Set<String> intersect = redisTemplate.opsForSet().intersect(key1, key2);

        // 并集
        Set<String> union = redisTemplate.opsForSet().union(key1, key2);

        // 差集
        Set<String> difference = redisTemplate.opsForSet().difference(key1, key2);

        log.info("集合1：{}，集合2：{}，两个集合的交集：{}，两个集合的并集：{}，两个集合的差集：{}", listOne, listTwo, intersect, union, difference);
    }
}
