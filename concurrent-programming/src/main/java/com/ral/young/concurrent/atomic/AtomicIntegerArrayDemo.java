package com.ral.young.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * {@link  java.util.concurrent.atomic.AtomicIntegerArray}
 *
 * @author renyunhui
 * @date 2022-09-27 9:08
 * @since 1.0.0
 */
public class AtomicIntegerArrayDemo {

    static int[] arr = new int[10];

    static AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(arr);

    public static void main(String[] args) {
        atomicIntegerArray.set(2, 1);
        // atomicIntegerArray 将 arr 进行了 clone，所以修改的是副本对象，不会影响到原对象数组
        // 源码：this.array = array.clone(); 克隆
        System.out.println(atomicIntegerArray.get(2));
        System.out.println(arr[2]);
    }
}
