package com.ral.young.night.jvm;

import javassist.CannotCompileException;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟 堆 栈 方法区 内存溢出的情况
 *
 * @author renyunhui
 * @date 2024-06-25 15:54
 * @since 1.0.0
 */
public class MemoryOverflowDemo {

    static List<Object> objects;
    public static void main(String[] args) throws CannotCompileException, InstantiationException, IllegalAccessException {
        // testHeapOutOfMemory();

        // testStackOutOfMemory(1);

        testMethodAreaOutOfMemory(DeadLockDemo.class);
    }

    public static void testHeapOutOfMemory() {
        // -Xmx10M -Xms10M -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError
        objects = new ArrayList<>();
        while (true) {
            // 在堆上创建大量的对象，导致 outOfMemory
            objects.add(new Object());
        }
    }

    public static void testStackOutOfMemory(int i) {
        // -Xmx10M -Xms10M -Xss256k -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError
        // 无限循环递归调用此方法，导致 stack overflow
        testStackOutOfMemory(i);
    }

    public static void testMethodAreaOutOfMemory(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        for (int i = 0; ; i++) {
            // 创建大量的类导致方法区内存溢出
            Object object = clazz.newInstance();
        }
    }
}
