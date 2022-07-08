package com.ral.young.aop.proxy.jdk;

import java.lang.reflect.Proxy;

/**
 * @author renyunhui
 * @date 2022-07-06 13:48
 * @since 1.0.0
 */
public class JdkDynamicProxyAnimal {

    public static Object getProxy(Object target) throws Exception {
        // 指定的类加载
        // 代理需要实现的接口，可指定多个，这是一个数组
        // 代理对象处理器
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new TargetInvoker(target));
    }
}
