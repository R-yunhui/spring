package com.ral.young.spring.aop;

import com.ral.young.spring.aop.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author renyunhui
 * @date 2023-02-17 11:00
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestServiceOne {

    @Log
    public void testOne() {
        log.info("执行 testOne 方法");
    }
}
