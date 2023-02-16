package com.ral.young.spring.ioc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author renyunhui
 * @date 2023-02-16 14:21
 * @since 1.0.0
 */
@Data
@Slf4j
public class User implements InitializingBean, DisposableBean, BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, ApplicationContextAware {
    
    private String name;
    
    private int id;

    public User() {
    }

    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @PostConstruct
    public void initOne() {
        log.info("bean 执行 @PostConstruct 标记的方法 ");
    }
    
    public void initTwo() {
        log.info("bean 执行配置的 initMethod 方法 ");
    }
    
    public void destroyTwo() {
        log.info("bean 执行配置的 destroyMethod 方法 ");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Bean 的初始化执行的方法
        log.info("bean 执行实现 InitializingBean 接口的 afterPropertiesSet() 方法 ");
    }

    @Override
    public void destroy() throws Exception {
        log.info("bean 执行实现 DisposableBean 接口的 destroy() 方法 ");
    }

    @Override
    public void setBeanClassLoader(@NotNull ClassLoader classLoader) {
        log.info("bean 执行实现 BeanClassLoaderAware 接口的方法");
    }

    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        log.info("bean 执行实现 BeanFactoryAware 接口的方法");
    }

    @Override
    public void setBeanName(@NotNull String name) {
        log.info("bean 执行实现 BeanNameAware 接口的方法");
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        log.info("bean 执行实现 ApplicationContextAware 接口的方法");
    }
}
