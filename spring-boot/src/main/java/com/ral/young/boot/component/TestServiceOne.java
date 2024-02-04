package com.ral.young.boot.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2024-01-29 16:56
 * @since 1.0.0
 */
@Component
@Slf4j
public class TestServiceOne implements InitializingBean {

    private TestServiceTwo testServiceTwo;

    @Autowired
    public void setTestServiceTwo(ObjectProvider<TestServiceTwo> testServiceTwoObjectProvider) {
        /*
         *  通过 ObjectProvider 进行注入主要是为了解决两个问题：
         *     1.容器中存在多个Bean时
         *     2.容器中不存在该 Bean，或者该 Bean 实例为 null
         */
        this.testServiceTwo = testServiceTwoObjectProvider.getIfAvailable(() -> {
            log.warn("Spring未加载此Bean，通过手动的方式进行创建");
            TestServiceTwo obj = new TestServiceTwo();
            obj.setName("NewTestServiceTwo");
            return obj;
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("TestServiceTwo Name:{}", testServiceTwo.getName());
    }
}
