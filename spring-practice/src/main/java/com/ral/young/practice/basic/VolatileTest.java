package com.ral.young.practice.basic;

/**
 * volatile 关键字保证了有序性和可见性
 * @author renyunhui
 * @date 2023-04-17 11:12
 * @since 1.0.0
 */
public class VolatileTest {

    private volatile VolatileTest instance;

    private volatile static int a = 1;

    private VolatileTest() {
    }

    public static void main(String[] args) {
        testA();
    }

    public static void testA() {
        Thread thread = new Thread(() -> {
            int c = a + 1;
            System.out.println(c);
        });
        thread.start();
    }

    public VolatileTest getInstance() {
        // double check
        // 第一次校验
        if (null == instance) {
            // 加锁
            synchronized (VolatileTest.class) {

                // 二次校验
                if (null == instance) {
                    /*
                     * 多线程情况下可能出现问题的地方 - 有序性
                     * new 对象的过程：
                     * 1.给对象分配内存
                     * 2.初始化对象
                     * 3.设置 instance 指向前面分配的内存地址，此时 instance != null
                     *
                     * 编译器优化：
                     * 1.给对象分配内存
                     * 2.设置 instance 指向前面分配的内存地址，此时 instance != null，但是对象并没有初始化完成
                     * 3.初始化对象
                     *
                     * 可以使用 volatile 修饰，保证有序性，通过内存屏障保证的有序性
                     *
                     * volatile 通过在字节码上添加 lock 前缀
                     * 1.Lock前缀的指令：JVM 会向处理器发送一条 lock 前缀的指令，将这个变量所在的缓存行立即写回系统内存；
                     * 2.通过缓存一致性协议，其他线程如果工作内存中存了该共享变量的值，就会失效；
                     * 3.其他线程会重新从主内存中获取最新的值；
                     *
                     * 【注】：lock前缀的指令在多核处理器下会引发两件事情
                     *  1.将当前处理器缓存行的数据写回到系统内存。
                     *  2.这个写回内存的操作会使在其他 CPU 里缓存了该内存地址的数据无效。
                     */
                    instance = new VolatileTest();
                }
            }
        }
        return instance;
    }
}
