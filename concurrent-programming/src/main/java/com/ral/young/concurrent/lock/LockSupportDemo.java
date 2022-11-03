package com.ral.young.concurrent.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * {@link java.util.concurrent.locks.LockSupport}
 *
 * @author renyunhui
 * @date 2022-09-19 14:14
 * @since 1.0.0
 */
@Slf4j
public class LockSupportDemo {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (;;) {
                log.info("当前线程:{}被阻塞", Thread.currentThread().getName());
                LockSupport.park();
                log.info("当前线程:{}被唤醒", Thread.currentThread().getName());
                break;
            }
        });

        t1.start();

        new Thread(() -> {
            // 唤醒 t1
            LockSupport.unpark(t1);
        }).start();
    }
}
