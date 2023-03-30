package com.ral.young.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author renyunhui
 * @date 2023-03-07 16:41
 * @since 1.0.0
 */
@Slf4j
public class LockDemo {

    static final Object o = new Object();

    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
       testLock();
    }

    public static void testSync() throws InterruptedException {
        AtomicInteger atomicInteger1 = new AtomicInteger();
        AtomicInteger atomicInteger2 = new AtomicInteger();
        Thread t1 = new Thread(new SyncTask(atomicInteger1));
        Thread t2 = new Thread(new SyncTask(atomicInteger2));

        t1.start();
        t2.start();

        Thread.sleep(100);
        // synchronized 不支持中断，t2 等待的时候，发出中断信息也不会有中断异常出现
        t2.interrupt();
        log.info("t2 的中断状态：{}",t2.isInterrupted());
        log.info("atomicInteger1:{}", atomicInteger1.get());
        log.info("atomicInteger2:{}", atomicInteger2.get());
    }

    public static void testLock() throws InterruptedException {
        AtomicInteger atomicInteger1 = new AtomicInteger();
        AtomicInteger atomicInteger2 = new AtomicInteger();
        Thread t1 = new Thread(new LockTask(atomicInteger1));
        Thread t2 = new Thread(new LockTask(atomicInteger2));

        t1.start();
        t2.start();

        Thread.sleep(100);
        // 支持中断，如果 t2 在等待获取锁的过程中被中断，则会抛出异常
        t2.interrupt();
        log.info("t2 的中断状态：{}",t2.isInterrupted());
        log.info("atomicInteger1:{}", atomicInteger1.get());
        log.info("atomicInteger2:{}", atomicInteger2.get());
    }

    public static class LockTask implements Runnable {

        private final AtomicInteger atomicInteger;

        public LockTask(AtomicInteger atomicInteger) {
            this.atomicInteger = atomicInteger;
        }

        @Override
        public void run() {
            try {
                lock.lockInterruptibly();
                log.info("{} 开始执行任务", Thread.currentThread().getName());

                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    // do something
                    atomicInteger.incrementAndGet();
                }

                log.info("{} 任务执行完毕", Thread.currentThread().getName());
            } catch (Exception e) {
                log.error("{} 任务处理异常", Thread.currentThread().getName());
            } finally {
                lock.unlock();
            }
        }
    }

    public static class SyncTask implements Runnable {

        private final AtomicInteger atomicInteger;

        public SyncTask(AtomicInteger atomicInteger) {
            this.atomicInteger = atomicInteger;
        }

        @Override
        public void run() {
            synchronized (o) {
                try {
                    log.info("{} 开始执行任务", Thread.currentThread().getName());

                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        // do something
                        atomicInteger.incrementAndGet();
                    }

                    log.info("{} 任务执行完毕", Thread.currentThread().getName());
                } catch (Exception e) {
                    log.error("{} 任务处理异常", Thread.currentThread().getName());
                }
            }
        }
    }
}
