package com.ral.young.night.spring.ioc.bean;

import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

import javax.annotation.PostConstruct;

/**
 *
 * @author renyunhui
 * @date 2024-06-07 15:27
 * @since 1.0.0
 */
@Data
@Slf4j
public class User implements InitializingBean, BeanNameAware, DisposableBean, SmartInitializingSingleton, FactoryBean<User>, BeanClassLoaderAware {

    private Long id;

    private String name;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 在所有 Bean 属性设置完成后进行初始化操作
        log.debug("===== Bean 的生命周期： 5.InitializingBean#afterPropertiesSet() 在所有 Bean 属性设置完成后进行初始化操作，修改 Bean 的属性");
        this.setName("Bob");
    }

    public void customInitMethod() {
        log.debug("====== Bean 的生命周期：  6.调用自定义的 init-method 方法");
    }

    @Override
    public void setBeanName(String name) {
        log.debug("====== Bean 的生命周期：  3.调用 BeanNameAware 的回调");
    }

    @Override
    public void destroy() throws Exception {
        log.debug("====== Bean 的生命周期：  10.容器关闭，调用 DisposableBean#destroy() 的回调");
    }

    public void customDestroy() throws Exception {
        log.debug("====== Bean 的生命周期：  11.调用自定义的 destroy() 方法");
    }

    @PostConstruct
    public void postConstructMethod() {
        log.debug("====== Bean 的生命周期：  调用 @PostConstruct 注解的方法");
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 在所有Bean 初始化完成之后的回调
        log.debug("扩展点：所有 Bean 初始化完成之后的回调");
    }

    @Override
    public User getObject() throws Exception {
        // 优先用工厂方法创建 Bean
        User user = new User();
        user.setId(IdUtil.getSnowflakeNextId());
        user.setName("FactoryName");
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return this.getClass();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        // 获取加载这个类的类加载器
        log.debug("====== Bean 的生命周期：  3.调用 BeanClassLoaderAware 的回调:{}", classLoader.getClass().getName());
    }
}
