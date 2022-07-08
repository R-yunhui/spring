package com.ral.young.aop.proxy.jdk;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理对象处理器
 *
 * @author renyunhui
 * @date 2022-07-06 13:47
 * @since 1.0.0
 */
@Slf4j
public class TargetInvoker implements InvocationHandler {

    /**
     * 代理持有的目标类
     */
    private Object target;

    public TargetInvoker(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("jdk 动态代理执行前 ~~~");
        // jdk动态代理是通过反射的方式调用目标方法的
        Object result = method.invoke(target, args);
        log.info("jdk 动态代理执行前 ~~~");
        return result;
    }
}
