package com.ral.young.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ral.young.handler.WsMessageBroadcaster;
import com.ral.young.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author renyunhui
 * @description 这是一个WebSocketServiceImpl类
 * @date 2024-09-20 15-06-24
 * @since 1.0.0
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    /**
     * 根据id存储会话
     */
    public static ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>(32);

    private static AtomicInteger connectionCount = new AtomicInteger(0);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void handlerEstablishConnection(String sessionId, WebSocketSession webSocketSession) {
        sessionMap.put(sessionId, webSocketSession);
        long count = connectionCount.incrementAndGet();
        log.info("新建 WebSocket 连接成功，【{}】，当前连接总数：{}", sessionId, count);

        // 订阅 redis 频道
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.pSubscribe((message, pattern) -> {
                String payload = String.valueOf(message);
                broadcastMessage(payload);
            }, new byte[][] { WsMessageBroadcaster.BROADCAST_CHANNEL.getBytes() });
            return null;
        });
    }

    @Override
    public void handlerCloseConnection(String sessionId, WebSocketSession webSocketSession) {
        if (ObjectUtil.isNotNull(sessionMap.remove(sessionId))) {
            sessionMap.remove(sessionId, webSocketSession);
            long count = connectionCount.decrementAndGet();
            log.info("关闭 WebSocket 连接成功，【{}】，当前连接总数：{}", sessionId, count);
        } else {
            log.warn("关闭 WebSocket 连接异常，【{}】", sessionId);
        }
    }

    @Override
    public void sendMessage(String sessionId, String message) {
        WebSocketSession webSocketSession = sessionMap.get(sessionId);
        if (ObjectUtil.isNull(webSocketSession)) {
            log.warn("获取不到 WebSocket 连接【{}】,无法发送消息:{}", sessionId, message);
            return;
        }

        if (!webSocketSession.isOpen()) {
            log.warn("WebSocket 连接【{}】 已关闭,无法发送消息:{}", sessionId, message);
            return;
        }

        try {
            webSocketSession.sendMessage(new TextMessage(message));
            log.info("向 WebSocket 连接【{}】发送成功", sessionId);
        } catch (IOException e) {
            log.error("向 WebSocket 连接【{}】发送失败，error：", sessionId, e);
        }
    }

    @Override
    public void broadcastMessage(String message) {
        if (CollUtil.isEmpty(sessionMap)) {
            log.warn("当前不存在 WebSocket 连接，无法广播消息");
            return;
        }

        sessionMap.forEach((k, v) -> sendMessage(k, message));
    }
}
