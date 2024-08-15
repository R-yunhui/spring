package com.ral.young.night.redis.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RDeque;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author renyunhui
 * @description 测试使用 redisson 实现延迟关闭订单信息
 * @date 2024-08-14 11-15-18
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService {

    @Resource
    private RedissonClient redissonClient;

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), ThreadFactoryBuilder.create().setNameFormat("delay-work-%d").get());

    public void testDelayQueue() throws InterruptedException {
        RBlockingDeque<Order> orderBlockingQueue = redissonClient.getBlockingDeque("order_delay_queue");
        RDelayedQueue<Order> orderDelayQueue = redissonClient.getDelayedQueue(orderBlockingQueue);
        IntStream.of(10).forEach(i -> {
            long expireTime = RandomUtil.randomLong(5000, 10000);
            Order order = Order.builder().orderId(IdUtil.getSnowflakeNextId()).orderName("macBook pro").createTime(DateUtil.current()).expireTime(expireTime).build();

            orderDelayQueue.offer(order, expireTime, TimeUnit.MILLISECONDS);
        });

        Thread.sleep(2000);

        EXECUTOR.execute(() -> {
            Order order;
            while ((order = orderDelayQueue.poll()) != null) {
                try {
                    log.info("执行订单关闭操作，当前关闭时间：{}，订单信息：{}", DateUtil.now(), JSONUtil.toJsonPrettyStr(order));
                } catch (Exception e) {
                    log.error("处理关闭订单操作失败,e", e);
                }
            }
        });
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class Order implements Serializable {

        private static final long serialVersionUID = 1L;

        private long orderId;

        private long expireTime;

        private long createTime;

        private String orderName;
    }
}
