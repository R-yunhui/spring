package com.ral.young.practice.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.NonNull;

/**
 * TODO
 *
 * @author renyunhui
 * @date 2022-11-16 14:03
 * @since 1.0.0
 */
@Slf4j
public class TestBeanFactoryPostProcessorOne implements PriorityOrdered, BeanDefinitionRegistryPostProcessor {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        log.info("TestBeanFactoryPostProcessorOne 执行");
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
