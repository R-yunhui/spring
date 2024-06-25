package com.ral.young.night.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 *
 * @author renyunhui
 * @date 2024-05-27 16:11
 * @since 1.0.0
 */
public class ConcurrentDemo {

    public static void main(String[] args) throws InterruptedException {
        /*
         * volatile：保证可见性和有序性
         *      1.通过内存屏障来禁止指令重排，保证有序性
         *      2.通过JVM向处理器发送一条带 LOCK 前缀的指令，将这个缓存中的变量回写到系统内存中。
         *        当一个变量被 volatile 修饰，每次数据发生变化之后，其值都会从线程的工作内存被强制刷新入主内存
         *
         * CAS：保证原子性（执行命令会锁 CPU 总线）
         */
        IntStream.range(0, 10).forEach(i -> {
            try {
                testOneAdd();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static final Semaphore semaphore = new Semaphore(10);

    private static final ReentrantLock lock = new ReentrantLock();

    static int num = 0;

    public static void testOneAdd() throws InterruptedException {
        // 模拟使用 semaphore 实现一个累加操作（会出现并发问题导致多线程执行 num++ 结果和预期不一致）
        // 使用 ReentrantLock 实现一个累加操作（不会出现并发问题）
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {

                try {
                    lock.lock();
                    for (int j = 0; j < 1000; j++) {
                        num++;
                    }

                } finally {
                    lock.unlock();
                }
            }).start();
        }

        Thread.sleep(1000);
        System.out.println("累加后的结果：" + num);
    }
}
