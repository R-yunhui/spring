package com.ral.young.spring.ioc;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * {@link org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter}
 *
 * @author renyunhui
 * @date 2023-02-16 14:19
 * @since 1.0.0
 */
@Component
@Slf4j
public class CustomInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(@NotNull Class<?> beanClass, @NotNull String beanName) throws BeansException {
        // 在 Bean 对象实例化之前执行，可以在此处直接创建一个对象（代理对象 or 正常对象）返回，不让它走 spring bean 的生命周期
        log.info("在 bean 对象实例化之前执行此方法");
        return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        log.info("在 bean 对象实例化之后执行此方法");
        return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public PropertyValues postProcessProperties(@NotNull PropertyValues pvs, @NotNull Object bean, @NotNull String beanName) throws BeansException {
        log.info("在 bean 对象属性填充之前执行此方法");
        return InstantiationAwareBeanPostProcessor.super.postProcessProperties(pvs, bean, beanName);
    }

}
