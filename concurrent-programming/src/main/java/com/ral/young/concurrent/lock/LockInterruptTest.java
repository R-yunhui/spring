package com.ral.young.concurrent.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link java.util.concurrent.locks.ReentrantLock}
 *
 * @author renyunhui
 * @date 2022-09-21 11:15
 * @since 1.0.0
 */
@Slf4j
public class LockInterruptTest {

    private static ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("当前线程:{} 准备获取锁", Thread.currentThread().getName());
            reentrantLock.lock();

            try {
                log.info("当前线程:{} 获取到锁", Thread.currentThread().getName());
                for (; ; ) {

                }
            } finally {
                reentrantLock.unlock();
                log.info("当前线程:{} 释放了锁", Thread.currentThread().getName());
            }
        });
        t1.start();

        Thread.sleep(2000);

        Thread t2 = new Thread(() -> {
            log.info("当前线程:{} 准备获取锁", Thread.currentThread().getName());
            // 中断线程 t1
            t1.interrupt();
            log.info("当前线程:{} 中断线程:{}", Thread.currentThread().getName(), t1.getName());
            // 尝试获取锁 - 只有等待 t1 释放了锁 t2 才能获取到锁，通过 reentrantLock.lock(); 不会抛出异常
            reentrantLock.lock();
            try {
                log.info("当前线程:{} 获取到锁", Thread.currentThread().getName());
            } finally {
                reentrantLock.unlock();
                log.info("当前线程:{} 释放了锁", Thread.currentThread().getName());
            }
        });

        Thread t4 = new Thread(() -> {
            log.info("当前线程:{} 准备获取锁", Thread.currentThread().getName());
            // 尝试获取锁 - 只有等待 t1 释放了锁 t4 才能获取到锁，通过 reentrantLock.lock(); 会抛出中断异常
            try {
                reentrantLock.lockInterruptibly();
                log.info("当前线程:{} 获取到锁", Thread.currentThread().getName());
            } catch (InterruptedException e) {
                // 可以在此处处理中断异常
                log.error("中断异常:{}", e.getMessage(), e);
            } finally {
                reentrantLock.unlock();
                log.info("当前线程:{} 释放了锁", Thread.currentThread().getName());
            }
        });
        t4.start();

        Thread.sleep(2000);

        Thread t3 = new Thread(() -> {
            log.info("当前线程:{} 准备获取锁", Thread.currentThread().getName());
            // 中断线程 t4，如果 t4 通过 reentrantLock.lockInterruptibly(); 进行获取锁，则会抛出中断异常
            t4.interrupt();
            log.info("当前线程:{} 中断线程:{}", Thread.currentThread().getName(), t1.getName());
        });
        t3.start();
    }
}
