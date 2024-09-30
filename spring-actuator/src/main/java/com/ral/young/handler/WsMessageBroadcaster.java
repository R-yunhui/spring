package com.ral.young.handler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @description 这是一个WsMessageBroadcaster类
 * @date 2024-09-20 15-29-24
 * @since 1.0.0
 */
@Component
public class WsMessageBroadcaster {


    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public static final String BROADCAST_CHANNEL = "broadcast-channel";

    public void broadcast(Object o) {
        redisTemplate.convertAndSend(BROADCAST_CHANNEL, o);
    }
}
