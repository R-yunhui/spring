package com.ral.young.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * redis 连接池 缓存预热
 *
 * @author renyunhui
 * @date 2022-07-22 14:20
 * @since 1.0.0
 */
@Slf4j
public class RedisCacheWarmUp {

    public static void main(String[] args) {
        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        // 设置连接池的最大连接数
        poolConfig.setMaxTotal(100);
        // 设置连接池的最大空闲连接数
        poolConfig.setMaxIdle(80);
        // 设置连接池的最小空闲连接数
        poolConfig.setMinIdle(10);

        JedisPool jedisPool = new JedisPool(poolConfig, "49.235.87.36", 6379, "default", "ryh123.0");
        List<Jedis> minIdles = new ArrayList<>();
        // 连接池并不是已启动就会创建 minIdle 个 redis 连接，而是在有客户端进行连接的过程中逐渐增加
        // 特殊情况：在程序启动的时候，就会有大量请求发生的情况，所以需要做连接池预热
        log.info("开始执行连接池的缓存预热");
        for (int i = 0, minIdle = poolConfig.getMinIdle(); i < minIdle; i++) {
            try {
                Jedis resource = jedisPool.getResource();
                // 执行 ping 命令
                resource.ping();
                minIdles.add(resource);
            } catch (Exception e) {
                log.error("连接池预热失败，获取 jedis 连接失败，errorMsg：{}", e.getMessage(), e);
            } finally {
                // 不做 jedis 连接的归还，否则之后 minIdles 只会有一个连接
            }
        }

        // 统一将预热的连接归还连接池
        log.info("统一将预热的连接归还连接池");
        for (Jedis jedis : minIdles) {
            jedis.close();
        }
    }
}
