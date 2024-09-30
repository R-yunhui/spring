package com.ral.young.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ral.young.bo.ResourceAlarmMessage;
import com.ral.young.enums.ResourceEnum;
import com.ral.young.service.WebSocketService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
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

    private static final AtomicInteger CONNECTION_COUNT = new AtomicInteger(0);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void handlerEstablishConnection(String sessionId, WebSocketSession webSocketSession) {
        sessionMap.put(sessionId, webSocketSession);
        long count = CONNECTION_COUNT.incrementAndGet();
        log.info("新建 WebSocket 连接成功，【{}】，当前服务端连接总数：{}", sessionId, count);
    }

    @Override
    public void handlerCloseConnection(String sessionId, WebSocketSession webSocketSession) {
        if (ObjectUtil.isNotNull(sessionMap.remove(sessionId))) {
            sessionMap.remove(sessionId, webSocketSession);
            long count = CONNECTION_COUNT.decrementAndGet();
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
    public void onMessage(@NonNull Message message, byte[] bytes) {
        try {
            String value = redisTemplate.getStringSerializer().deserialize(message.getBody());
            if (StrUtil.isNotBlank(value)) {
                log.info("从 redis 处消费到消息，推送 ws 客户端");
                ResourceAlarmMessage alarmMessage = JSONUtil.toBean(value, ResourceAlarmMessage.class);
                if (ObjectUtil.isNull(alarmMessage)) {
                    log.warn("从 redis 消费的消息转换为 alarmMessage 异常，消息：{}", value);
                    return;
                }

                ResourceEnum resourceEnum = alarmMessage.getResourceEnum();
                if (ResourceEnum.PLATFORM_AUTH.equals(resourceEnum)) {
                    // 如果是平台授权过期，则告知所有以及连接的 ws 客户端
                    sendMessageToAll(value);
                } else if (ResourceEnum.TENANT_AUTH.equals(resourceEnum)) {
                    // 如果是租户授权过期，则告知所有属于当前租户的 ws 客户端
                    sendMessageToTenant(value, alarmMessage.getTenantId());
                } else {
                    // 其它情况，则按照 tenantId::userId 获取客户端会话发送消息
                    String sessionId = alarmMessage.getTenantId() + "::" + alarmMessage.getUserId();
                    sendMessage(sessionId, value);
                }
            }
        } catch (Exception e) {
            log.error("从 redis 处消费到消息，推送 ws 客户端异常， e：", e);
        }
    }

    public void sendMessageToAll(String message) {
        if (MapUtil.isEmpty(sessionMap)) {
            log.warn("暂未存在 ws 客户端连接");
            return;
        }

        sessionMap.values().forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("向 WebSocket 连接【{}】发送失败，error：", webSocketSession.getId(), e);
            }
        });
    }

    public void sendMessageToTenant(String message, Long tenantId) {
        if (MapUtil.isEmpty(sessionMap)) {
            log.warn("暂未存在 ws 客户端连接");
            return;
        }

        sessionMap.forEach((k, v) -> {
            // 只发给当前租户所属用户连接的 ws 客户端
            if (k.startsWith(String.valueOf(tenantId))) {
                sendMessage(k, message);
            }
        });
    }
}
