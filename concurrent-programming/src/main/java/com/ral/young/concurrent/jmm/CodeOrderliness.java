package com.ral.young.concurrent.jmm;

/**
 * 并发三大特性：有序性
 *
 * @author renyunhui
 * @date 2022-09-13 12:16
 * @since 1.0.0
 */
public class CodeOrderliness {

    private static volatile int a = 0;
    private static volatile int b = 0;
    private static volatile int x = 0;
    private static volatile int y = 0;

    public static void main(String[] args) throws InterruptedException {
        int idx = 1;
        for (; ; ) {
            a = 0;
            b = 0;
            x = 0;
            y = 0;

            Thread t1 = new Thread(() -> {
                a = 1;
                x = b;
            });

            Thread t2 = new Thread(() -> {
                b = 1;
                y = a;
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();
            idx++;

            System.out.println("第 " + idx + " 执行操作,(" + x + "," + y + ")");
            /*
             * 发生指令重排才会出现 x = y = 0
             * 使用 volatile（依赖内存屏障） 关键字修饰保证不会发生指令重排
             * 也可以手动添加内存屏障 - 依赖 Unsafe 这个类（魔术类）
             */
            if (x == 0 && y == 0) {
                System.err.println("发生了指令重排");
                break;
            }
        }
    }
}
