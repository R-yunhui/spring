package com.ral.young.collection;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.IdUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author renyunhui
 * @date 2022-07-26 10:51
 * @since 1.0.0
 */
public class TestList {

    static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(3, 10, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNamePrefix("test-").build());

    public static void main(String[] args) {
        // 初始化内部存储元素的数组为空 {}
        List<Integer> list = new ArrayList<>();
        // 添加元素之后，容量变为默认值 10
        System.out.println(list.add(1));

        // 超过指定容量，则扩容 1.5倍数
        // newSize = oldSize + oldSize >> 1

        // 多线程并发操作 list
        List<String> arrayList = new ArrayList<>();
        EXECUTOR.execute(() -> {
            // 一个线程死循环添加元素到 list 中
            for (; ; ) {
                arrayList.add(IdUtil.fastUUID());
            }
        });

        EXECUTOR.execute(() -> {
            // 一个先从死循环遍历获取 list 中的元素 （迭代器或者增强 for 循环）
            for (; ; ) {
                for (String s : arrayList) {
                    System.out.println(s);
                }
            }
        });

        // 结论：并发操作 list ，抛出：ConcurrentModificationException   modCount != expectedModCount
        // 【注】：迭代器遍历以及 for each 遍历会出现
    }
}
