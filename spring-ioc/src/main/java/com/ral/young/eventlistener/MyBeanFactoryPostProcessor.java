package com.ral.young.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 自定义 BeanFactoryPostProcessor
 *
 * @author renyunhui
 * @date 2022-06-29 10:38
 * @since 1.0.0
 */
@Slf4j
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor, SmartInitializingSingleton {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 通过自定义一个 BeanFactoryPostProcessor 设置属性的自动装配
        RootBeanDefinition beanDefinition = (RootBeanDefinition) beanFactory.getBeanDefinition("");
        // 按照名称进行自动装配
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 可以在 Spring 中所有 Bean 初始化完成之后进行调用
        log.info("Spring中所有Bean初始化完成,回调 org.springframework.beans.factory.SmartInitializingSingleton.afterSingletonsInstantiated 方法");
    }
}
