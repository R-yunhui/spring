package com.ral.young.jvm;

import org.openjdk.jol.info.ClassLayout;

/**
 * 查看对象信息
 *
 * @author renyunhui
 * @date 2022-07-28 11:06
 * @since 1.0.0
 */
public class ObjectInfo {

    public static void main(String[] args) {
        ClassLayout objectLayout = ClassLayout.parseInstance(new Object());
        System.out.println(objectLayout.toPrintable());

        System.out.println();

        ClassLayout arrLayout = ClassLayout.parseInstance(new int[]{});
        System.out.println(arrLayout.toPrintable());

        System.out.println();

        ClassLayout testLayout = ClassLayout.parseInstance(new Test());
        System.out.println(testLayout.toPrintable());
    }

    public static class Test {

                       // 8B mark word
                       // 4B Klass Pointer   如果关闭压缩-XX:-UseCompressedClassPointers或-XX:-UseCompressedOops，则占用8B
        int id;        // 4B
        String name;   // 4B  如果关闭压缩-XX:-UseCompressedOops，则占用8B
        byte b;        // 1B  内部的对齐填充占用 3B  - 空间损失 3B
        Object o;      // 4B  如果关闭压缩-XX:-UseCompressedOops，则占用8B

        // 8的倍数的对齐填充，则需要填充 4B
        // 空间损失：3B + 4B = 7B
    }
}
