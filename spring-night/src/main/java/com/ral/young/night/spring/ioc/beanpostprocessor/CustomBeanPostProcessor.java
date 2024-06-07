package com.ral.young.night.spring.ioc.beanpostprocessor;

import com.ral.young.night.spring.ioc.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 自定义BeanPostProcessor
 *
 * @author renyunhui
 * @date 2024-06-07 16:04
 * @since 1.0.0
 */
@Slf4j
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (User.class.equals(bean.getClass())) {
            log.info("===== Bean 的生命周期： 4.调用 BeanPostProcessor 的前置处理方法");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (User.class.equals(bean.getClass())) {
            log.info("===== Bean 的生命周期： 7.调用 BeanPostProcessor 的后置处理方法");
        }
        return bean;
    }
}
