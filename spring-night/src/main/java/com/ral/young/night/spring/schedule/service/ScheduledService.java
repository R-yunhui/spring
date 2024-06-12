package com.ral.young.night.spring.schedule.service;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author renyunhui
 * @date 2024-06-12 13:50
 * @since 1.0.0
 */
@Service(value = "scheduledService")
@Slf4j
public class ScheduledService {

    @Scheduled(fixedRate = 1000)
    public void testOne() {
        // 固定周期
        log.info("定时执行 testOne fixedRate 时间：{}", DateUtil.now());
    }

    @Scheduled(fixedDelay = 3000)
    public void testTwo() {
        // 固定延迟
        log.info("定时执行 testTwo fixedDelay 时间：{}", DateUtil.now());
    }
}
