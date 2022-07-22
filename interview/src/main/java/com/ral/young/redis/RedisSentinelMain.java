package com.ral.young.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.stream.IntStream;

/**
 * redis 哨兵模式示例
 *
 * @author renyunhui
 * @date 2022-07-21 11:16
 * @since 1.0.0
 */
@Slf4j
public class RedisSentinelMain {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void test() {
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        IntStream.range(0, Integer.MAX_VALUE).forEach(i -> {
            try {
                Thread.sleep(1000);
                redisTemplate.opsForValue().set("test::" + i, String.valueOf(i));
                log.info(redisTemplate.opsForValue().get("test::" + i));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("执行失败,errorMsg:{}", e.getMessage(), e);
            }
        });
    }
}
