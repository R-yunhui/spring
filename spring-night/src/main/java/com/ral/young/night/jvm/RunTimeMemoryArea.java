package com.ral.young.night.jvm;

/**
 * JVM的运行时内存区域
 *
 * @author renyunhui
 * @date 2024-06-05 15:24
 * @since 1.0.0
 */
public class RunTimeMemoryArea {

    public static void main(String[] args) {
        /*
         * jdk1.8 中，JVM的运行时内存区域包括：
         * 堆（字符串常量池）
         * 栈（本地方法栈，Java虚拟机栈）  线程私有
         * 程序计数器  线程私有
         *
         * 方法区（运行时常量池）元空间的特点是可以根据应用程序的需要动态调整其大小，因此更加灵活。
         * 它能够有效地避免了永久代的内存溢出问题，并且可以减少垃圾回收的压力。
         * 元空间的内存使用量受限于操作系统对本地内存的限制。
         */
    }
}
