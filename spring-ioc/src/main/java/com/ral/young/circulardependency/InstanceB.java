package com.ral.young.circulardependency;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @date 2022-06-23 14:16
 * @since 1.0.0
 */
public class InstanceB {

    @Resource
    private InstanceA instanceA;

    public InstanceB() {
        System.out.println("InstanceB 实例化");
    }

    public void say() {
        System.out.println("I am InstanceA");
    }
}
