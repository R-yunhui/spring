package com.ral.young.handler;

import lombok.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author renyunhui
 * @description 这是一个WebSocketHandler类
 * @date 2024-09-20 14-57-29
 * @since 1.0.0
 */
public class CustomWebSocketHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession webSocketSession) throws Exception {
        // 连接建立之后
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession webSocketSession, @NonNull WebSocketMessage<?> webSocketMessage) throws Exception {
        // 处理客户端消息
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession webSocketSession, @NonNull Throwable throwable) throws Exception {
        // 处理传输错误
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession webSocketSession, @NonNull CloseStatus closeStatus) throws Exception {
        // 连接关闭之后
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
