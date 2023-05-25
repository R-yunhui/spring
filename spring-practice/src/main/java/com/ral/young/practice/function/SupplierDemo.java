package com.ral.young.practice.function;

import cn.hutool.core.util.IdUtil;

import java.util.function.Supplier;

/**
 * {@link java.util.function.Supplier}
 *
 * java.util.function.Supplier <T>接口仅包含一个无参的方法：T get()，用来获取一个泛型参数指定类型的对象数据
 *
 * 该接口被称为生产型接口,指定接口的泛型是什么类型,那么get方法就会产生什么类型的数据
 *
 * @author renyunhui
 * @date 2023-05-25 14:09
 * @since 1.0.0
 */
public class SupplierDemo {

    public static void main(String[] args) {
        SupplierDemo demo = new SupplierDemo();
        demo.testSupplier(demo::function);
    }

    public void testSupplier(Supplier<String> supplier) {
        // 通过定义的函数来获取一个结果，不需要参数
        String s = supplier.get();
        System.out.println(s);
    }

    public String function() {
        return IdUtil.fastSimpleUUID();
    }
}
