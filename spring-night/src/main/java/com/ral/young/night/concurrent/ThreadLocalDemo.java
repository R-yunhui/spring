package com.ral.young.night.concurrent;

/**
 * {@link ThreadLocal}
 *
 * @author renyunhui
 * @date 2024-05-27 15:40
 * @since 1.0.0
 */
public class ThreadLocalDemo {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 父子线程之间的值传递（不包含线程池）
     * 在子线程被创建的同时会拷贝父线程 ThreadLocal 中的数据
     */
    private static final ThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        /*
         * ThreadLocal 作用：
         * 1.解决并发问题
         * 2.在线程中传递数据
         *
         * 应用场景：
         * 1.用户身份信息存储
         * 2.线程安全
         * 3.日志上下文存储
         * 4.traceId 存储
         * 5.数据库 session
         * 6.PageHelper分页
         */
        // 以下示例未移除 ThreadLocal 可能会造成内存泄漏，正式使用务必要进行移除
        // testThreadLocal();
        // testThreadLocalTwo();
        // testThreadLocalThree();
        testThreadLocalFour();
    }

    /**
     * ThreadLocal 测试
     * @throws InterruptedException 中断异常
     */
    public static void testThreadLocal() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            // 绑定了当前线程
            threadLocal.set(Thread.currentThread().getName());
            System.out.println("t1线程执行，将数据存入 ThreadLocal");

            // 休眠 3s
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("在 t1 线程中获取 ThreadLocal的数据：" + threadLocal.get());
        });

        t1.start();

        Thread.sleep(1000);
        // 在主线程中获取不到数据，ThreadLocal 在存入数据的同时绑定了线程
        System.out.println("在主线程中获取 ThreadLocal中的数据：" + threadLocal.get());
    }

    /**
     * ThreadLocal 测试二
     * @throws InterruptedException 中断异常
     */
    public static void testThreadLocalTwo() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            // 绑定了当前线程
            threadLocal.set(Thread.currentThread().getName());
            System.out.println("t1线程执行，将数据存入 ThreadLocal");

            // 休眠 3s
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("在 t1 线程中获取 ThreadLocal的数据：" + threadLocal.get());
        });

        Thread t2 = new Thread(() -> {
            // 绑定了当前线程
            threadLocal.set(Thread.currentThread().getName());
            System.out.println("t2线程执行，将数据存入 ThreadLocal");

            // 休眠 3s
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 每个线程分别获取到自己设置的数据
            System.out.println("在 t2 线程中获取 ThreadLocal的数据：" + threadLocal.get());
        });

        t1.start();
        t2.start();

        Thread.sleep(1000);
        // 在主线程中获取不到数据，ThreadLocal 在存入数据的同时绑定了线程
        System.out.println("在主线程中获取 ThreadLocal中的数据：" + threadLocal.get());
    }

    /**
     * ThreadLocal 测试三  父子线程，值无法传递
     * @throws InterruptedException 中断异常
     */
    public static void testThreadLocalThree() throws InterruptedException {
        // 主线程设置数据
        threadLocal.set(Thread.currentThread().getName());
        Thread t1 = new Thread(() -> {
            // 使用 ThreadLocal 在子线程中获取不到父线程的数据
            System.out.println("在 t1 线程中第一次获取 ThreadLocal的数据：" + threadLocal.get());

            // 绑定了当前线程
            threadLocal.set(Thread.currentThread().getName());
            System.out.println("t1线程执行，将数据存入 ThreadLocal");

            // 休眠 3s
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("在 t1 线程第二次中获取 ThreadLocal的数据：" + threadLocal.get());
        });

        t1.start();

        Thread.sleep(1000);
        // ThreadLocal 在存入数据的同时绑定了线程
        System.out.println("在主线程中获取 ThreadLocal中的数据：" + threadLocal.get());
    }

    /**
     * ThreadLocal 测试四  父子线程值传递
     * @throws InterruptedException 中断异常
     */
    public static void testThreadLocalFour() throws InterruptedException {
        // 主线程设置数据
        inheritableThreadLocal.set(Thread.currentThread().getName());
        Thread t1 = new Thread(() -> {
            // 使用 inheritableThreadLocal 在子线程中获取不到父线程的数据
            System.out.println("在 t1 线程中第一次获取 ThreadLocal的数据：" + inheritableThreadLocal.get());

            // 绑定了当前线程
            inheritableThreadLocal.set(Thread.currentThread().getName());
            System.out.println("t1线程执行，将数据存入 ThreadLocal");

            // 休眠 3s
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("在 t1 线程第二次中获取 ThreadLocal的数据：" + inheritableThreadLocal.get());
        });

        t1.start();

        Thread.sleep(1000);
        // inheritableThreadLocal 在存入数据的同时绑定了线程
        System.out.println("在主线程中获取 ThreadLocal中的数据：" + inheritableThreadLocal.get());
    }
}
