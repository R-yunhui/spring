package com.ral.young.study.spring.ioc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.NonNull;

/**
 * 自定义 BeanFactory 后置处理器
 *
 * @author renyunhui
 * @date 2023-11-20 14:29
 * @since 1.0.0
 */
public class MyBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("实现 PriorityOrdered 接口");
        BeanDefinition beanDefinition = new RootBeanDefinition();
        // 实现 BeanDefinitionRegistryPostProcessor 接口的 postProcessBeanDefinitionRegistry 在 BeanDefinition 注册完成之后再进行 BeanDefinition 的注册
        beanDefinition.setBeanClassName(Car.class.getName());
        beanDefinition.setAttribute("name", "劳斯莱斯");
        registry.registerBeanDefinition("car", beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition car = beanFactory.getBeanDefinition("car");
        Object name = car.getAttribute("name");
        System.out.println(name);
        System.out.println(car.getBeanClassName());
    }

    @Override
    public int getOrder() {
        return -3;
    }
}
