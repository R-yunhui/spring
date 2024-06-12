package com.ral.young.night.spring.aop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author renyunhui
 * @date 2024-06-12 16:32
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService {

    @Async
    public void testOne() {
        log.info("{} 开始执行任务", Thread.currentThread().getName());
    }
}
