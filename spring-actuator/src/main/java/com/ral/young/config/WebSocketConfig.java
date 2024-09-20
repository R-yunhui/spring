package com.ral.young.config;

import com.ral.young.handler.CustomWebSocketHandler;
import com.ral.young.interceptor.WebSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author renyunhui
 * @description 这是一个WebSocketConfig类
 * @date 2024-09-20 14-54-40
 * @since 1.0.0
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CustomWebSocketHandler customWebSocketHandler;

    private final WebSocketInterceptor webSocketInterceptor;

    public WebSocketConfig(CustomWebSocketHandler customWebSocketHandler, WebSocketInterceptor webSocketInterceptor) {
        this.customWebSocketHandler = customWebSocketHandler;
        this.webSocketInterceptor = webSocketInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 配置处理器和前缀
        registry.addHandler(customWebSocketHandler, "websocket/ops")
                // 配置拦截器
                .addInterceptors(webSocketInterceptor)
                // 允许所有请求
                .setAllowedOrigins("*");
    }
}
