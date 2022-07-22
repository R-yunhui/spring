package com.ral.young.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 适用 jedis 操作 redis 单机示例
 *
 * @author renyunhui
 * @date 2022-07-21 10:13
 * @since 1.0.0
 */
@Slf4j
public class JedisSingleMain {

    public static void main(String[] args) {
        // 默认账号 default
        try (JedisPool jedisPool = new JedisPool("49.235.87.36", 6379, "default", "ryh123.0");) {
            Jedis jedis = jedisPool.getResource();

            // 从 jedisPool 中获取一个连接操作 redis
            jedis.set("product:10016", "10");
            String count = jedis.get("product:10016");
            log.info("获取到的商品数量：{}", count);

            // 适用管道批量打包执行命令，可以减少网络 io 的开销，但是不能保证事务的执行，某个操作执行失败，会继续执行后面的操作
            // 用 pipeline 方式打包命令发送，redis必须在处理完所有命令前先缓存起所有命令的处理结果，打包的命令越多，缓存消耗内存也越多。所以并不是打包的命令越多越好。
            Pipeline pipeline = jedis.pipelined();
            IntStream.range(1, 10).forEach(i -> {
                pipeline.incr("test:pipeline");
                pipeline.set("test:name" + i, String.valueOf(i));
            });
            List<Object> objects = pipeline.syncAndReturnAll();
            objects.forEach(x -> log.info("管道执行结果:{}", x));

            // lua 脚本示例
            // lua脚本命令执行方式：redis-cli --eval /tmp/test.lua , 10
            // 初始化商品10016的库存
            jedis.set("product_count_10016", "15");
            String script = " local count = redis.call('get', KEYS[1]) " +
                    " local a = tonumber(count) " +
                    " local b = tonumber(ARGV[1]) " +
                    " if a >= b then " +
                    "   redis.call('set', KEYS[1], a-b) " +
                    "   return 1 " +
                    " end " +
                    " return 0 ";
            Object obj = jedis.eval(script, Collections.singletonList("product_count_10016"), Collections.singletonList("10"));
            log.info("lua 脚本执行结果:{}", obj);
        } catch (Exception e) {
            log.error("操作redis 失败,errorMsg:{}", e.getMessage(), e);
        }
    }

}
