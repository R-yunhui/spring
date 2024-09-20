package com.ral.young.handler;

import com.ral.young.service.WebSocketService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author renyunhui
 * @description 这是一个WebSocketHandler类
 * @date 2024-09-20 14-57-29
 * @since 1.0.0
 */
@Slf4j
@Component
public class CustomWebSocketHandler implements WebSocketHandler {

    @Resource
    private WebSocketService webSocketService;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession webSocketSession) throws Exception {
        // 连接建立之后
        Object id = webSocketSession.getAttributes().get("id");
        if (Objects.isNull(id)) {
            log.warn("连接【{}】未携带标识参数，不进行连接建立", webSocketSession.getUri());
            return;
        }

        webSocketService.handlerEstablishConnection(id.toString(), webSocketSession);
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession webSocketSession, @NonNull WebSocketMessage<?> webSocketMessage) throws Exception {
        // 处理客户端消息
        Object id = webSocketSession.getAttributes().get("id");
        if (Objects.isNull(id)) {
            log.warn("连接【{}】未携带标识参数，不进行消息处理", webSocketSession.getUri());
            return;
        }

        log.info("webSocket连接【{}】接收到消息：{}", id, webSocketMessage.getPayload());
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession webSocketSession, @NonNull Throwable throwable) throws Exception {
        // 处理传输错误
        Object id = webSocketSession.getAttributes().get("id");
        if (Objects.isNull(id)) {
            log.warn("连接【{}】未携带标识参数，不进行异常处理", webSocketSession.getUri());
            return;
        }

        log.error("连接【{}】发生异常：", id, throwable);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession webSocketSession, @NonNull CloseStatus closeStatus) throws Exception {
        // 连接关闭之后
        Object id = webSocketSession.getAttributes().get("id");
        if (Objects.isNull(id)) {
            log.warn("连接【{}】未携带标识参数，不进行连接关闭", webSocketSession.getUri());
            return;
        }

        webSocketService.handlerCloseConnection(id.toString(), webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
