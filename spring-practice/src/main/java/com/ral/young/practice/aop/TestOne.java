package com.ral.young.practice.aop;

import com.ral.young.practice.aop.annotation.JuniorLog;
import com.ral.young.practice.aop.annotation.MediumLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author renyunhui
 * @date 2023-05-24 11:02
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestOne {

    @JuniorLog
    @MediumLog
    public void test() {
        log.info("TestOne#test() 源方法调用");
    }
}
