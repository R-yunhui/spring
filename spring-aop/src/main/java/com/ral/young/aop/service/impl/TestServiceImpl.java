package com.ral.young.aop.service.impl;

import com.ral.young.aop.annotation.MyCat;
import com.ral.young.aop.annotation.MyLog;
import com.ral.young.aop.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 测试类
 *
 * @author renyunhui
 * @date 2022-07-01 16:20
 * @since 1.0.0
 */
@Slf4j
@Service(value = "testService")
public class TestServiceImpl implements ITestService {

    @MyLog(name = "testLog")
    @MyCat(name = "波斯猫")
    @Override
    public void testLog() {
        log.info("TestService testLog() 开始打印日志");
    }
}
