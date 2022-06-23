package com.ral.young.circulardependency;

import javax.annotation.Resource;

/**
 *
 * @author renyunhui
 * @date 2022-06-23 14:15
 * @since 1.0.0
 */
public class InstanceA {

    @Resource
    private InstanceB instanceB;

    public InstanceA() {
        System.out.println("InstanceA 实例化");
    }

    public void say() {
        System.out.println("I am InstanceA");
    }
}
