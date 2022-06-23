package com.ral.young.circulardependency;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;

/**
 * 模拟动态代理创建的 BeanPostProcessor
 *
 * @author renyunhui
 * @date 2022-06-23 15:43
 * @since 1.0.0
 */
public class JdkProxyPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().equals(InstanceA.class)) {
            System.out.println("创建动态代理对象");
            return new InstanceA();
        }
        return bean;
    }
}
