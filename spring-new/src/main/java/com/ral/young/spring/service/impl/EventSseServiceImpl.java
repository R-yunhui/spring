package com.ral.young.spring.service.impl;

import cn.hutool.core.util.IdUtil;
import com.ral.young.spring.service.EventSseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SSE服务实现类 - 用于实时推送告警事件
 */
@Slf4j
@Service
public class EventSseServiceImpl implements EventSseService {

    /**
     * 存储所有的SSE连接,key为clientId
     */
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * 心跳调度器
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * SSE连接超时时间(毫秒)
     */
    @Value("${sse.timeout:60000}")
    private long timeout;

    /**
     * SSE心跳间隔(毫秒)
     */
    @Value("${sse.heartbeat.interval:15000}")
    private long heartbeatInterval;

    /**
     * 最大允许的客户端连接数
     */
    @Value("${sse.max.clients:1000}")
    private int maxClients;

    /**
     * 创建新的SSE连接
     *
     * @return SseEmitter实例
     */
    @Override
    public SseEmitter createEmitter() {
        // 检查连接数限制
        if (emitterMap.size() >= maxClients) {
            log.warn("超过最大客户端连接数限制: {}", maxClients);
            throw new RuntimeException("服务器连接数已达上限，请稍后重试");
        }

        String clientId = IdUtil.fastSimpleUUID();
        SseEmitter emitter = new SseEmitter(timeout);

        // 设置各种回调
        emitter.onCompletion(() -> {
            log.info("客户端连接完成，移除客户端: {}", clientId);
            removeEmitter(clientId);
        });

        emitter.onTimeout(() -> {
            log.info("客户端连接超时，移除客户端: {}", clientId);
            removeEmitter(clientId);
        });

        emitter.onError(ex -> {
            log.error("客户端连接异常: {}", ex.getMessage());
            removeEmitter(clientId);
        });

        // 存储emitter
        emitterMap.put(clientId, emitter);

        // 发送连接成功消息
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connect success"));

            // 启动心跳
            startHeartbeat(clientId, emitter);
        } catch (IOException e) {
            log.error("发送连接成功消息失败", e);
            removeEmitter(clientId);
        }

        log.info("app新客户端连接成功: {}, 当前连接数: {}", clientId, emitterMap.size());
        return emitter;
    }

    /**
     * 向所有客户端推送告警事件
     *
     * @param event 告警事件详情
     */
    @Override
    public void sendEventToEmitters(Object event) {
        if (emitterMap.isEmpty()) {
            return;
        }

        emitterMap.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("event")
                        .data(event));
                log.info("向app客户端: {} 发送事件成功", clientId);
            } catch (IOException e) {
                log.error("向app客户端: {} 发送事件失败", clientId, e);
                removeEmitter(clientId);
            }
        });
    }

    /**
     * 启动心跳任务
     */
    private void startHeartbeat(String clientId, SseEmitter emitter) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("ping"));
            } catch (IOException e) {
                log.error("发送心跳消息失败，app客户端: {}", clientId, e);
                removeEmitter(clientId);
            }
        }, heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 移除指定的SSE连接
     */
    private void removeEmitter(String clientId) {
        SseEmitter emitter = emitterMap.remove(clientId);
        if (emitter != null) {
            emitter.complete();
        }
    }

    /**
     * 定时清理已断开的连接
     */
    @Scheduled(fixedRate = 300000) // 5分钟执行一次
    public void cleanupEmitters() {
        emitterMap.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IOException e) {
                log.info("检测到断开的连接，进行清理: {}", clientId);
                removeEmitter(clientId);
            }
        });
    }

    /**
     * 服务关闭时清理资源
     */
    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        emitterMap.forEach((clientId, emitter) -> {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.error("关闭SSE连接失败: {}", clientId, e);
            }
        });
        emitterMap.clear();
    }
}