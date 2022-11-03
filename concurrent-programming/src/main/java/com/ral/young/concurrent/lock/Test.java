package com.ral.young.concurrent.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试
 *
 * @author renyunhui
 * @date 2022-09-21 17:28
 * @since 1.0.0
 */
@Slf4j
public class Test {

    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1  = new Thread(() -> {
            REENTRANT_LOCK.lock();
            try {
                log.info("当前线程:{} 获取到锁", Thread.currentThread().getName());
                for (;;) {

                }
            } finally {
                REENTRANT_LOCK.unlock();
            }
        });

        t1.start();

        Thread.sleep(1000);

        Thread t2  = new Thread(() -> {
            REENTRANT_LOCK.lock();
            try {
                log.info("当前线程:{} 获取到锁", Thread.currentThread().getName());
                for (;;) {

                }
            } finally {
                REENTRANT_LOCK.unlock();
            }
        });

        t2.start();
    }
}
