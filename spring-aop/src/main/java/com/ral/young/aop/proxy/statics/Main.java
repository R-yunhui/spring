package com.ral.young.aop.proxy.statics;

/**
 *
 * @author renyunhui
 * @date 2022-07-06 13:45
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        // 通过调用静态代理实现方法的增强
        // 在程序运行之前，代理类字节码.class就已编译好，通常一个静态代理类也只代理一个目标类，代理类和目标类都实现相同的接口
        JdkProxyAnimal proxyAnimal = new JdkProxyAnimal(new Dog());
        proxyAnimal.call();
    }
}
