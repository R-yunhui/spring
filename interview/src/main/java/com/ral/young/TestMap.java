package com.ral.young;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.IdUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * HashMap
 * 一个线程对 map 执行插入操作
 * 一个线程对 map 使用迭代器进行遍历
 *
 * @author renyunhui
 * @date 2022-07-14 17:13
 * @since 1.0.0
 */
public class TestMap {

    static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(3, 10, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNamePrefix("test-").build());


    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>(16);
        EXECUTOR.execute(() -> {
            // 一个线程对 map 死循环的方式执行插入操作
            for (; ; ) {
                // 每次操作会修改 modCount
                map.put(IdUtil.fastSimpleUUID(), IdUtil.fastUUID());
            }
        });

        EXECUTOR.execute(() -> {
            // 一个线程对 map 死循环的方式使用迭代器进行遍历
            for (; ; ) {
                // 初始化迭代器的时候会定义一个：expectedModCount = modCount
                for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                    System.out.println(stringStringEntry.getKey());
                }
            }
        });

        // 结论：抛出：ConcurrentModificationException   modCount != expectedModCount
    }
}
