package com.ral.young.aop.proxy.cglib;

import org.springframework.cglib.proxy.Enhancer;

/**
 * Cglib动态代理
 *
 * @author renyunhui
 * @date 2022-07-06 13:58
 * @since 1.0.0
 */
public class CglibProxy {

    public static Object getProxy(Class<?> clazz){
        Enhancer enhancer = new Enhancer();
        // 设置类加载
        enhancer.setClassLoader(clazz.getClassLoader());
        // 设置被代理类
        enhancer.setSuperclass(clazz);
        // 设置方法拦截器
        enhancer.setCallback(new TargetInterceptor());
        // 创建代理类
        return enhancer.create();
    }

}
