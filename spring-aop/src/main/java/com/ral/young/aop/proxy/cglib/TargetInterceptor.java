package com.ral.young.aop.proxy.cglib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib动态代理的方法拦截
 *
 * @author renyunhui
 * @date 2022-07-06 13:55
 * @since 1.0.0
 */
@Slf4j
public class TargetInterceptor implements MethodInterceptor {

    /**
     * 方法拦截
     *
     * @param o           代理类对象
     * @param method      当前被代理拦截的方法
     * @param objects     拦截方法的参数
     * @param methodProxy 代理类对应目标类的代理方法
     * @return 方法执行结果
     * @throws Throwable 异常信息
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        log.info("CGLIB 动态代理之前~~~");
        // 直接调用父类方法的实现即可
        Object result = methodProxy.invokeSuper(o, objects);
        log.info("CGLIB 动态代理之后~~~");
        return result;
    }
}
