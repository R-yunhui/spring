package com.ral.young.night.concurrent;

/**
 *
 * @author renyunhui
 * @date 2024-05-27 16:11
 * @since 1.0.0
 */
public class ConcurrentDemo {

    public static void main(String[] args) {
        /*
         * volatile：保证可见性和有序性
         *      1.通过内存屏障来禁止指令重排，保证有序性
         *      2.通过JVM向处理器发送一条带 LOCK 前缀的指令，将这个缓存中的变量回写到系统内存中。
         *        当一个变量被 volatile 修饰，每次数据发生变化之后，其值都会从线程的工作内存被强制刷新入主内存
         *
         * CAS：保证原子性（执行命令会锁 CPU 总线）
         */
    }
}
