package com.ral.young.concurrent;

import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * {@link java.util.concurrent.locks.ReentrantLock}
 *
 * @author renyunhui
 * @date 2024-03-13 10:24
 * @since 1.0.0
 */
public class ReentrantLockTest {

    public static void main(String[] args) {
        ReentrantLockTest test = new ReentrantLockTest();
        // 尝试 10 次
        IntStream.range(0, 10).forEach(i -> {
            try {
                test.testNonFairLock();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    final ReentrantLock nonFairLock = new ReentrantLock();

    public void testNonFairLock() throws InterruptedException {
        // 验证非公平锁
        // 非公平锁：如果当前 CLH 队列中存在等待锁释放的线程，当锁释放的时候同时来了一个线程尝试获取锁会产生竞争而不是排队
        Thread t1 = new Thread(() -> {
            nonFairLock.lock();
            try {
                System.out.println("线程：" + Thread.currentThread().getName() + "，获取到锁");
                Thread.sleep(5000);
                System.out.println("线程：" + Thread.currentThread().getName() + "，释放锁");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                nonFairLock.unlock();
            }
        });
        t1.start();

        Thread.sleep(1000);
        Thread t2 = new Thread(() -> {
            nonFairLock.lock();
            try {
                System.out.println("线程：" + Thread.currentThread().getName() + "，获取到锁");
                Thread.sleep(500);
                System.out.println("线程：" + Thread.currentThread().getName() + "，释放锁");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                nonFairLock.unlock();
            }
        });
        t2.start();

        Thread.sleep(1000);
        Thread t3 = new Thread(() -> {
            nonFairLock.lock();
            try {
                System.out.println("线程：" + Thread.currentThread().getName() + "，获取到锁");
                Thread.sleep(500);
                System.out.println("线程：" + Thread.currentThread().getName() + "，释放锁");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                nonFairLock.unlock();
            }
        });
        t3.start();

        Thread.sleep(10000);
        System.out.println("==================================================");
    }
}
