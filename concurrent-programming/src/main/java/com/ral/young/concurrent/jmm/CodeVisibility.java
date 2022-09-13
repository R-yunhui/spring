package com.ral.young.concurrent.jmm;

/**
 * 并发三大特性：可见性
 *
 * @author renyunhui
 * @date 2022-09-13 10:47
 * @since 1.0.0
 */
public class CodeVisibility {

    /**
     * 主内存中的共享变量
     * 添加 volatile 保证：可见性，有序性
     * 添加了 volatile 保证能否及时的看到，不加的话线程也能看到，但是时间确定不了
     * 在字节码层面加了一个flag：ACC_VOLATILE
     */
    private static boolean initFlag = false;

    /**
     * 加入 volatile 进行修饰
     * 由于缓存行大小为 64 byte， 一个缓存行中包含了 initFlag，count，可能 count 在某一时间失效，导致去主内存中重新加载 count 的同时，将 initFlag 也加载到同一个缓存行中
     */
    private static volatile int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            // 当前线程 t1 看不到 线程 t2 的工作内存的变化，所以感知不到 initFlag 发生变化
            // 加入 volatile 关键字进行修饰，可以保证可见性
            while (!initFlag) {
                count++;
            }

            System.out.println("线程：" + Thread.currentThread().getName() + " 执行到此,感知到 initFlag 的变化,count：" + count);
        });
        t1.start();

        Thread.sleep(500);

        Thread t2 = new Thread(CodeVisibility::refresh);
        t2.start();
    }

    private static void refresh() {
        System.out.println("线程：" + Thread.currentThread().getName() + " 开始执行 refresh() 方法");
        // 线程 2，将共享变量 initFlag = false 从主内存中拷贝一个副本到自己的工作内存，执行 initFlag = true，在刷新回主内存
        initFlag = true;
        System.out.println("线程：" + Thread.currentThread().getName() + " 完成执行 refresh() 方法");
    }
}
