package com.ral.young.practice.ioc;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author renyunhui
 * @date 2023-05-23 14:54
 * @since 1.0.0
 */
@Service
public class TestOne {

    @Resource
    private TestTwo testTwo;


    @Async
    public void test() {
        System.out.println("testOne test");
    }
}
