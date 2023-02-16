package com.ral.young.spring.ioc;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * {@link BeanPostProcessor}
 *
 * @author renyunhui
 * @date 2023-02-16 14:26
 * @since 1.0.0
 */
@Slf4j
public class CustomBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        log.info("在 bean 对象初始化之前执行");
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        log.info("在 bean 对象初始化之后执行");
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
