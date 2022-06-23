package com.ral.young.circulardependency;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 循环依赖
 *
 * @author renyunhui
 * @date 2022-06-23 14:11
 * @since 1.0.0
 */
public class CycleDependency {

    /**
     * 一级缓存 - 存放成熟的 Bean
     */
    private static Map<String, Object> singleTonObjects = new ConcurrentHashMap<>();

    /**
     * 二级缓存 - 为了将成熟的 Bean 和纯净的 Bean 进行分离，避免获取到不完整的 Bean
     */
    private static Map<String, Object> earlySingleTonObjects = new ConcurrentHashMap<>();

    /**
     * 三级缓存 - 存放一个函数接口，创建
     */
    private static Map<String, ObjectFactory> singleTonFactories = new ConcurrentHashMap<>();

    /**
     * 判断循环依赖的标识 - 正在创建的单例 Bean 的哈希表
     */
    private static Set<String> singleTonCurrentlyCreation = new HashSet<>();

    private static final Map<String, AbstractBeanDefinition> BEAN_DEFINITION_MAPS = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        // 创建动态代理的时机：
        // 1.存在循环依赖则是在实例化之后调用 BeanPostProcessor 创建代理对象
        // 2.不存在循环依赖则是在初始化之后调用 BeanPostProcessor 创建代理对象
        loadBeanDefinitions();

        // 循环获取 Bean
        for (String key : BEAN_DEFINITION_MAPS.keySet()) {
            getBean(key);
        }

        InstanceA instanceA = (InstanceA) getBean("instanceA");
        instanceA.say();
    }

    private static void loadBeanDefinitions() {
        BEAN_DEFINITION_MAPS.put("instanceA", new RootBeanDefinition(InstanceA.class));
        BEAN_DEFINITION_MAPS.put("instanceB", new RootBeanDefinition(InstanceB.class));
    }

    private static Object getBean(String beanName) throws InstantiationException, IllegalAccessException {
        Object singleTon = getSingleTon(beanName);
        if (null != singleTon) {
            return singleTon;
        }

        // 标记当前 Bean 正在创建
        singleTonCurrentlyCreation.add(beanName);

        RootBeanDefinition beanDefinition = (RootBeanDefinition) BEAN_DEFINITION_MAPS.get(beanName);
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 通过无参构造进行实例化
        Object instance = beanClass.newInstance();

        // 创建动态代理 - 依赖 BeanPostProcessor - 只在循环依赖的情况下载实例化之后创建代理对象
        // 但是spring 还是希望在非循环依赖的情况下的 Bean 实在初始化之后创建代理对象
        // 通过三级缓存进行解耦，存放一个函数接口 - 在初始化完成之后创建动态代理
        Object finalInstance = instance;
        singleTonFactories.put(beanName, () -> Objects.requireNonNull(new JdkProxyPostProcessor().postProcessAfterInitialization(finalInstance, beanName)));

        // 属性注入
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Resource resource = declaredField.getAnnotation(Resource.class);
            // 属性上是否存在 @Resource 注解
            if (null != resource) {
                declaredField.setAccessible(true);
                Object instanceB = getBean(declaredField.getName());
                declaredField.set(instance, instanceB);
            }
        }

        // 初始化
        // 循环依赖的情况：在这里创建动态代理的时机晚了，会导致或取到的 Bean 不是代理的 Bean对象
        if (earlySingleTonObjects.containsKey(beanName)) {
            // 循环依赖 + 存在代理的时候，保证放到一级缓存的是代理对象
            instance = earlySingleTonObjects.get(beanName);
        }

        // 放入一级缓存
        singleTonObjects.put(beanName, instance);

        // 移除二级缓存和三级缓存
        earlySingleTonObjects.remove(beanName);
        singleTonObjects.remove(beanName);
        return instance;
    }

    private static Object getSingleTon(String beanName) {
        // 先从一级缓存中获取
        Object singleTon = singleTonObjects.get(beanName);
        if (null == singleTon && singleTonCurrentlyCreation.contains(beanName)) {
            // 说明是循环依赖
            singleTon = earlySingleTonObjects.get(beanName);
            // 如果二级缓存没有，则从三级缓存中获取
            if (null == singleTon) {
                // 从三级缓存中获取
                ObjectFactory<?> objectFactory = singleTonFactories.get(beanName);
                if (null != objectFactory) {
                    singleTon = objectFactory.getObject();

                    // 放入二级缓存 - 即动态代理
                    earlySingleTonObjects.put(beanName, singleTon);

                    // 从三级缓存中移除
                    singleTonObjects.remove(beanName);
                }
            }
        }
        return singleTon;
    }
}
