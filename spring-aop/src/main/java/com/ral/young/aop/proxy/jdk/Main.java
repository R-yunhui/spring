package com.ral.young.aop.proxy.jdk;

import com.ral.young.aop.proxy.statics.Animal;
import com.ral.young.aop.proxy.statics.Dog;

/**
 *
 * @author renyunhui
 * @date 2022-07-06 13:50
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Dog dog = new Dog();
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        Animal dogProxy = (Animal) JdkDynamicProxyAnimal.getProxy(dog);
        dogProxy.call();
    }
}
