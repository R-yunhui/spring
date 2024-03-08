package com.ral.young.year.postprocessor;

import com.ral.young.year.bean.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 自定义 BeanFactoryPostProcessor
 *
 * @author renyunhui
 * @date 2024-03-07 10:10
 * @since 1.0.0
 */
@Slf4j
@Component
public class TestBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        log.info("=== TestBeanFactoryPostProcessor postProcessBeanDefinitionRegistry ===");
        // 重写此方法可以注册 BeanDefinition
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(Car.class);
        registry.registerBeanDefinition("car", beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("=== TestBeanFactoryPostProcessor postProcessBeanFactory ===");
    }
}
