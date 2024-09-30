package com.ral.young.config;

import com.ral.young.handler.WsMessageBroadcaster;
import com.ral.young.service.WebSocketService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }


    @Bean
    public MessageListenerAdapter messageListenerAdapter(WebSocketService receiver) {
        return new MessageListenerAdapter(receiver);
    }

    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        // 配置redis消息监听器和分组
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new PatternTopic(WsMessageBroadcaster.BROADCAST_CHANNEL));
        return redisMessageListenerContainer;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 设置 key 的序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        // 设置 value 的序列化方式
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));

        // 设置 hash 的 key 的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置 hash 的 value 的序列化方式
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));

        template.afterPropertiesSet();
        return template;
    }

}
