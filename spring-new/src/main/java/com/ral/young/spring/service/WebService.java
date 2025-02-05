package com.ral.young.spring.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.ral.young.spring.entity.vo.EventVO;
import com.ral.young.spring.util.RandomDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @description 模拟推送事件，通过 sse 接口进行推送
 * @date 2025-02-05 14-33-50
 * @since 1.0.0
 */
@Service
@Slf4j
public class WebService {

    @Resource
    private EventSseService eventSseService;

    /**
     * 模拟每5秒推送一次事件到SSE
     */
    @Scheduled(fixedRate = 5000, initialDelay = 5000)
    public void simulateEventPush() {
        try {
            // 构建事件数据
            EventVO eventVO = EventVO.builder()
                    .eventId(IdUtil.getSnowflakeNextId())
                    .time(DateUtil.now())
                    .eventName(RandomDataUtil.generateRandomName())
                    .build();

            // 调用SSE服务推送事件
            eventSseService.sendEventToEmitters(eventVO);
        } catch (Exception e) {
            log.error("事件推送失败", e);
        }
    }
}
