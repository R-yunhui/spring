package com.ral.young.spring.basic.dubbo.provider.impl;

import com.ral.young.spring.basic.dubbo.provider.api.TestServiceApi;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author renyunhui
 * @description 这是一个TestServiceApiImpl类
 * @date 2024-11-28 11-20-28
 * @since 1.0.0
 */
@DubboService
public class TestServiceApiImpl implements TestServiceApi {

    @Override
    public String sayHello() {
        return "hello world";
    }
}
