package com.ral.young.redis;

import cn.hutool.core.util.IdUtil;
import com.ral.young.redis.config.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;

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

        // 关闭容器
        applicationContext.close();
    }
}
