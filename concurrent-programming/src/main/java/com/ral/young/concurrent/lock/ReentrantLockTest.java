package com.ral.young.concurrent.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入
 *
 * @author renyunhui
 * @date 2022-09-20 15:15
 * @since 1.0.0
 */
@Slf4j
public class ReentrantLockTest {

    private static ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            reentrantLock.lock();

            log.info("{} 第一次加锁", Thread.currentThread().getName());

            reentrantLock.lock();

            log.info("{} 第二次加锁", Thread.currentThread().getName());
            /*
             * 验证 ReentrantLock 的可重入性
             * 1.相同线程可重入，state + 1
             * 2.加几次锁，需要释放几次锁，否则其他线程依然获取不到，释放一次 state - 1
             */
            reentrantLock.unlock();
            reentrantLock.unlock();
        });

        Thread t2 = new Thread(() -> {
            reentrantLock.lock();

            log.info("{} 第一次加锁", Thread.currentThread().getName());

            // 需要等待 t1 释放了所有的锁，才可以获取到锁
            reentrantLock.unlock();
        });

        t1.join();
        t1.start();
        t2.start();
    }

}
