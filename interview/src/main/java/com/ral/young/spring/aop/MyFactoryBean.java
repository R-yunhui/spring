package com.ral.young.spring.aop;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2023-02-17 15:20
 * @since 1.0.0
 */
@Component
public class MyFactoryBean implements FactoryBean<MyFactoryBean> {
    @Override
    public MyFactoryBean getObject() throws Exception {
        return new MyFactoryBean();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
}
