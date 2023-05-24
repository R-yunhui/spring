package com.ral.young.practice.sprint.postprocessor.bean;

import cn.hutool.core.date.DateUtil;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * {@link SmartInstantiationAwareBeanPostProcessor}
 *
 * @author renyunhui
 * @date 2023-05-24 10:42
 * @since 1.0.0
 */
@Component
public class MySmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    @NonNull
    public Object getEarlyBeanReference(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        System.out.println("SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference 调用：" + DateUtil.now());
        return bean;
    }
}
