package com.ral.young.interceptor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author renyunhui
 * @description 这是一个WebSocketInterceptor类
 * @date 2024-09-20 15-36-26
 * @since 1.0.0
 */
@Slf4j
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    /**
     * websocket连接校验
     */
    public final static String ID_REGEX = "^[0-9]*$";

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {
        // 提取请求参数
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            // 校验连接参数，满足要求则连接
            String id = servletServerHttpRequest.getServletRequest().getParameter("id");
            log.info("ws连接  id:{}", id);
            if (!StringUtils.hasLength(id)) {
                log.warn("WebSocket连接未携带key和id标识，拒绝连接");
                return false;
            }

            if (!Pattern.matches(ID_REGEX, id)) {
                log.warn("id参数【{}】非法，不连接", id);
                return false;
            }

            attributes.put("id", id);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, Exception exception) {

    }
}
