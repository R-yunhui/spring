package com.ral.young.practice.sprint.postprocessor.bean;

import cn.hutool.core.date.DateUtil;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * {@link org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor}
 *
 * @author renyunhui
 * @date 2023-05-24 10:26
 * @since 1.0.0
 */
@Component
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(@NonNull Class<?> beanClass, @NonNull String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println("一、InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation 调用：" + DateUtil.now());
        }
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println("二、InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation 调用：" + DateUtil.now());
        }
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(@NonNull PropertyValues pvs, @NonNull Object bean, @NonNull String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println("三、InstantiationAwareBeanPostProcessor#postProcessProperties 调用：" + DateUtil.now());
        }
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println("七、InstantiationAwareBeanPostProcessor#postProcessBeforeInitialization 调用：" + DateUtil.now());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println("十一、InstantiationAwareBeanPostProcessor#postProcessAfterInitialization 调用：" + DateUtil.now());
        }
        return bean;
    }
}
