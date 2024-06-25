package com.ral.young.night.jvm;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 对象内存分配
 *
 * @author renyunhui
 * @date 2024-06-25 14:03
 * @since 1.0.0
 */
@Slf4j
public class AllocMemoryDemo {

    public static void main(String[] args) {
        // 模拟创建百万对象，验证栈上分配对象占用内存
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            // 第一次：-Xmx4G -Xms4G -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError
            // 第二次：-Xmx4G -Xms4G -XX:+DoEscapeAnalysis -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError （开启逃逸分析）
            // 通过 jmap -histo pid 查看两次不同的jvm 参数配置堆上创建的 user 对象数量不一致
            allocMemoryOnStack();
        }

        System.out.println("模拟创建百万对象，验证栈上分配对象占用内存，耗时：" + (System.currentTimeMillis() - start) + "ms");
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            log.error("中断异常");
        }
    }

    public static void allocMemoryOnStack() {
        /*
         * 模拟栈上分配对象
         * 依赖开启 逃逸分析 + 标量替换
         * 标量（Scalar）是指一个无法再分解成更小的数据的数据。Java中的原始数据类型就是标量。
         * 相对的，那些还可以分解的数据叫做聚合量（Aggregate），Java中的对象就是聚合量，因为他可以分解成其他聚合量和标量。
         */
        User user = new User();
    }

    static class User {
        int id;

        String username;
    }
}
