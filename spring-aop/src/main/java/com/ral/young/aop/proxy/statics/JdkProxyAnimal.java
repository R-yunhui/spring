package com.ral.young.aop.proxy.statics;

import lombok.extern.slf4j.Slf4j;

/**
 * 代理类
 *
 * @author renyunhui
 * @date 2022-07-06 13:43
 * @since 1.0.0
 */
@Slf4j
public class JdkProxyAnimal implements Animal {

    private Animal animal;

    public JdkProxyAnimal(Animal animal) {
        this.animal = animal;
    }

    @Override
    public void call() {
        log.info("狗叫");
        animal.call();
    }
}
