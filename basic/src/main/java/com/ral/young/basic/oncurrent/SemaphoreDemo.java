package com.ral.young.basic.oncurrent;

import java.util.concurrent.Semaphore;

/**
 * {@link java.util.concurrent.Semaphore}
 *
 * Semaphore 是线程同步的辅助类，可以限制当前访问自身的线程个数，并提供了同步机制。通过 Semaphore 可以控制同时访问资源的线程个数。
 * 并不能保证多线程线程并发安全，需要自己进行控制
 *
 * @author renyunhui
 * @date 2023-05-29 15:53
 * @since 1.0.0
 */
public class SemaphoreDemo {

    public static void main(String[] args) throws InterruptedException {
        SemaphoreDemo demo = new SemaphoreDemo();
        demo.testSemaphore();
    }

    /**
     * 顺序打印 1 2 3
     */
    private void testSemaphore() throws InterruptedException {
        Semaphore semaphore1 = new Semaphore(1);
        Semaphore semaphore2 = new Semaphore(1);
        Semaphore semaphore3 = new Semaphore(1);

        // 主线程先获取 semaphore2 semaphore3 的许可，保证 semaphore1 先执行
        semaphore2.acquire();
        semaphore3.acquire();

        Thread t1 = new Thread(() -> {
           while (true) {
               // 如果可以拿到 semaphore1 的许可，执行任务1，并释放 semaphore2 让任务2执行
               if (semaphore1.tryAcquire()) {
                   System.out.print(1 + " ");

                   semaphore2.release();
               }
           }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                // 如果任务1执行完成，则会释放 semaphore2 的许可，则当前可以获取 semaphore2 的许珂，执行任务2，并释放 semaphore3 让任务3执行
                if (semaphore2.tryAcquire()) {
                    System.out.print(2 + " ");

                    semaphore3.release();
                }
            }
        });

        Thread t3 = new Thread(() -> {
            while (true) {
                // 如果任务2执行完成，则会释放 semaphore3 的许可，则当前可以获取 semaphore3 的许可，执行任务 3，并释放 semaphore1 让任务1再次执行
                if (semaphore3.tryAcquire()) {
                    System.out.print(3 + "\n");
                    semaphore1.release();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }
}
