package com.ral.young.night.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis 配置类
 *
 * @author renyunhui
 * @date 2024-06-20 14:09
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        // 默认是 0 号数据库，一共 16个数据
        configuration.setDatabase(10);
        configuration.setPort(6379);
        configuration.setHostName("redis.dev.internal.seeyon.site");
        configuration.setPassword("9f776b5be306aaac29b423d9f");
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(10)
                .setAddress("redis://redis.dev.internal.seeyon.site:6379")
                .setPassword("9f776b5be306aaac29b423d9f");
        return Redisson.create(config);
    }
}
