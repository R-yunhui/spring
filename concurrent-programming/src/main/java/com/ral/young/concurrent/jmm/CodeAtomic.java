package com.ral.young.concurrent.jmm;

import java.util.stream.IntStream;

/**
 * 并发三大特性：原子性
 *
 * @author renyunhui
 * @date 2022-09-13 11:29
 * @since 1.0.0
 */
public class CodeAtomic {

    private static int count = 0;

    private static final Object OBJECT = new Object();

    public static void main(String[] args) throws InterruptedException {
        IntStream.range(0, 10).forEach(x -> {
            // volatile 不能保证原子性，i++ 并不是一个原子操作
            new Thread(() -> {
                // 通过加锁解决原子性
                synchronized (OBJECT) {
                    /*
                     * count++：非原子性操作，中间可能存在线程上下文切换
                     * 1.执行 count + 1 的操作
                     * 2.执行 count = count + 1 的操作
                     *
                     * volatile无法解决原子性：由于 count++ 分为两不操作，如果 t1 执行完操作，将数据写回主内存，由于MESI协议，缓存行失效
                     * 则 t2 需要通过缓存行重新从主内存中 load 数据到工作内存中，可能 count + 1 操作已经在寄存器中执行完毕了，
                     * 重新加载数据到工作内存之后覆盖了之前修改后的 count 值，导致最后的结果不符合预期
                     */
                    IntStream.range(0, 1000).forEach(o -> count++);
                }
            }).start();
        });

        Thread.sleep(1000);
        System.out.println("i：" + count);
    }
}
