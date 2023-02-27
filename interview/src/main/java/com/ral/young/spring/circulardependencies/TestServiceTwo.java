package com.ral.young.spring.circulardependencies;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @date 2023-02-16 19:44
 * @since 1.0.0
 */
@Service
public class TestServiceTwo {

    @Resource
    private TestServiceOne testServiceOne;

    @Async
    public void testTwo() {

    }
}
