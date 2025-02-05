package com.ral.young.spring.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE服务接口 - 用于实时推送告警事件
 */
public interface EventSseService {

    /**
     * 创建新的SSE连接
     *
     * @return SseEmitter实例
     */
    SseEmitter createEmitter();

    /**
     * 向所有客户端推送告警事件
     *
     * @param event 告警事件详情
     */
    void sendEventToEmitters(Object event);
} 