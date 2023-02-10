package com.ral.young.concurrent;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

/**
 * {@link java.util.concurrent.Semaphore}
 *
 * @author renyunhui
 * @date 2023-02-08 15:47
 * @since 1.0.0
 */
@Slf4j
public class SemaphoreTestTwo {

    static Semaphore semaphore1 = new Semaphore(1);
    static Semaphore semaphore2 = new Semaphore(1);
    static Semaphore semaphore3 = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {
        /*
         * 多线程循环顺序打印 1 2 3
         */
        // 首先主线程先获取 semaphore2 semaphore3 的许可
        semaphore2.acquire();
        semaphore3.acquire();

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0, n = 10; i < n; i++) {
                    // t1 获取 semaphore1 的许可
                    semaphore1.acquire();
                    log.info(1 + " ");
                    // t1 释放 semaphore2 的许可，让 t2 得以执行。后续以此类推即可
                    semaphore2.release();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0, n = 10; i < n; i++) {
                    semaphore2.acquire();
                    log.info(2 + " ");
                    semaphore3.release();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                for (int i = 0, n = 10; i < n; i++) {
                    semaphore3.acquire();
                    log.info(3 + " \n");
                    semaphore1.release();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t3.start();

        log.info("stop the task");
    }
}
