package com.ral.young.concurrent.interview;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程顺序循环打印 1 2 3
 *
 * @author renyunhui
 * @date 2022-09-12 16:16
 * @since 1.0.0
 */
public class SequentialPrint {

    public static void main(String[] args) throws InterruptedException {
        // 使用 semaphore 实现：多线程循环打印 1 2 3
        // testSemaphore();

        // 使用原子操作类实现：多线程循环打印 123
        // testAtomic();

        // 使用 volatile + synchronized 实现：多线程循环打印 123
        // testSync();

        // 使用 volatile + ReentrantLock 实现：多线程循环打印 123
        testLock();
    }

    private static void testSemaphore() throws InterruptedException {
        Semaphore semaphore1 = new Semaphore(1);
        Semaphore semaphore2 = new Semaphore(1);
        Semaphore semaphore3 = new Semaphore(1);

        semaphore2.acquire();
        semaphore3.acquire();

        // 使用信号量实现多线程循环打印 1 2 3
        Thread t1 = new Thread(() -> {
            while (true) {
                if (semaphore1.tryAcquire()) {
                    System.out.println();
                    System.out.print(1 + " ");
                    semaphore2.release();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                if (semaphore2.tryAcquire()) {
                    System.out.print(2 + " ");
                    semaphore3.release();
                }
            }
        });

        Thread t3 = new Thread(() -> {
            while (true) {
                if (semaphore3.tryAcquire()) {
                    System.out.print(3 + " ");
                    semaphore1.release();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }

    private static void testAtomic() {
        AtomicLong idx = new AtomicLong(0);
        Thread t1 = new Thread(() -> {
            while (true) {
                if (idx.get() % 3 == 1) {
                    System.out.println();
                    System.out.print(1 + " ");
                    idx.incrementAndGet();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                if (idx.get() % 3 == 2) {
                    System.out.print(2 + " ");
                    idx.incrementAndGet();
                }
            }
        });

        Thread t3 = new Thread(() -> {
            while (true) {
                if (idx.get() % 3 == 0) {
                    System.out.print(3 + " ");
                    idx.incrementAndGet();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }

    private static volatile int idx = 1;

    private static void testSync() {
        Object key = new Object();

        Thread t1 = new Thread(() -> {
            while (true) {
                if (idx == 1) {
                    synchronized (key) {
                        if (idx == 1) {
                            System.out.println();
                            System.out.print(1 + " ");
                            idx = 2;
                        }
                    }
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                if (idx == 2) {
                    synchronized (key) {
                        if (idx == 2) {
                            System.out.print(2 + " ");
                            idx = 3;
                        }
                    }
                }
            }
        });

        Thread t3 = new Thread(() -> {
            while (true) {
                if (idx == 3) {
                    synchronized (key) {
                        if (idx == 3) {
                            System.out.print(3 + " ");
                            idx = 1;
                        }
                    }
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }

    private static volatile int flag = 1;

    private static void testLock() {
        ReentrantLock lock = new ReentrantLock();

        Thread t1 = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    if (flag == 1) {
                        System.out.println();
                        System.out.print(1 + " ");
                        flag = 2;
                    }
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    if (flag == 2) {
                        System.out.print(2 + " ");
                        flag = 3;
                    }
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread t3 = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    if (flag == 3) {
                        System.out.print(3 + " ");
                        flag = 1;
                    }
                } finally {
                    lock.unlock();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }
}
