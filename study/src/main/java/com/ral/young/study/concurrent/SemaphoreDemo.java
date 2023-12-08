package com.ral.young.study.concurrent;

import cn.hutool.core.date.DateUtil;

import java.util.concurrent.Semaphore;

/**
 * {@link java.util.concurrent.Semaphore}
 *
 * @author renyunhui
 * @date 2023-12-06 10:32
 * @since 1.0.0
 */
public class SemaphoreDemo {

    public static void main(String[] args) throws InterruptedException {
        testThree();
    }

    private static void testOne() throws InterruptedException {
        Semaphore semaphore = new Semaphore(10);

        semaphore.acquire(10);

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("当前线程" + Thread.currentThread().getName() + " 获取到许可,开始执行任务,时间：" + DateUtil.now());
                semaphore.release();
            });

            t.start();
        }

        // 主线程休眠，等待其它线程的创建
        Thread.sleep(2000);
        // 释放许可
        System.out.println("当前等待获取锁的线程数量：" + semaphore.getQueueLength());
        semaphore.release(1);

        Thread.sleep(2000);
        System.out.println("当前等待获取锁的线程数量：" + semaphore.getQueueLength());
    }

    private static void testTwo() throws InterruptedException {
        // 顺序循环打印 1 2 3
        Semaphore semaphoreOne = new Semaphore(1);
        Semaphore semaphoreTwo = new Semaphore(1);
        Semaphore semaphoreThree = new Semaphore(1);

        semaphoreTwo.acquire();
        semaphoreThree.acquire();

        new Thread(() -> {
            for (;;) {
                try {
                    semaphoreOne.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.print(1 + " ");

                semaphoreTwo.release();
            }
        }).start();

        new Thread(() -> {
            for (;;) {
                try {
                    semaphoreTwo.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.print(2 + " ");

                semaphoreThree.release();
            }
        }).start();

        new Thread(() -> {
            for (;;) {
                try {
                    semaphoreThree.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.print(3 + " \n");

                semaphoreOne.release();
            }
        }).start();
    }

    private static void testThree() throws InterruptedException {
        // 模拟死锁
        Semaphore semaphoreOne = new Semaphore(1);
        Semaphore semaphoreTwo = new Semaphore(1);

        Thread t1 = new Thread(() -> {
            try {
                semaphoreOne.acquire();

                System.out.println(Thread.currentThread().getName() + " 获取到 semaphoreOne 的许可,开始执行任务");
                Thread.sleep(2000);

                System.out.println(Thread.currentThread().getName() + " 准备获取 semaphoreTwo 的许可,执行下一个任务");
                semaphoreTwo.acquire();

                System.out.println(Thread.currentThread().getName() + " 释放持有的所有许可");
                semaphoreOne.release();
                semaphoreTwo.release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            try {
                semaphoreTwo.acquire();

                System.out.println(Thread.currentThread().getName() + " 获取到 semaphoreTwo 的许可,开始执行任务");
                Thread.sleep(2000);

                System.out.println(Thread.currentThread().getName() + " 准备获取 semaphoreOne 的许可,执行下一个任务");
                semaphoreOne.acquire();

                System.out.println(Thread.currentThread().getName() + " 释放持有的所有许可");
                semaphoreOne.release();
                semaphoreTwo.release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t2.start();

        Thread.sleep(10000);
        if (semaphoreOne.getQueueLength() != 0 && semaphoreTwo.getQueueLength() != 0) {
            System.out.println("任务被阻塞,可能出现死锁的情况");
            t1.interrupt();
            t2.interrupt();
        } else {
            System.out.println("执行完毕所有任务");
        }
    }
}
