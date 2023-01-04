package com.ral.young.circulardependency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 测试循环依赖
 *
 * @author renyunhui
 * @date 2023-01-03 14:06
 * @since 1.0.0
 */
@Service
public class TestServiceTwo {

    @Autowired
    private TestServiceOne testServiceOne;

    public void test() {

    }
}
