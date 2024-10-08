package com.ral.young.service;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author renyunhui
 * @description 这是一个WebSocketService类
 * @date 2024-09-20 15-01-10
 * @since 1.0.0
 */
public interface WebSocketService extends MessageListener {

    /**
     * 处理建立连接
     *
     * @param sessionId        自定义的会话id
     * @param webSocketSession websocket会话
     */
    void handlerEstablishConnection(String sessionId, WebSocketSession webSocketSession);

    /**
     * 处理关闭连接
     *
     * @param sessionId        自定义的会话id
     * @param webSocketSession websocket会话
     */
    void handlerCloseConnection(String sessionId, WebSocketSession webSocketSession);

    /**
     * 发送消息
     *
     * @param sessionId 自定义的会话id
     * @param message   消息
     */
    void sendMessage(String sessionId, String message);
}
