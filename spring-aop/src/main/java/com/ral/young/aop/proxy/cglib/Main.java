package com.ral.young.aop.proxy.cglib;

import com.ral.young.aop.proxy.statics.Animal;
import com.ral.young.aop.proxy.statics.Dog;
import org.springframework.cglib.core.DebuggingClassWriter;

/**
 *
 * @author renyunhui
 * @date 2022-07-06 13:56
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\renyunhui\\workspace\\spring\\cglib");
        // 相比于 JDK 动态代理的实现，CGLIB 动态代理不需要实现与目标类一样的接口，而是通过方法拦截的方式实现代理
        Animal dog = (Animal) CglibProxy.getProxy(Dog.class);
        dog.call();
    }
}
