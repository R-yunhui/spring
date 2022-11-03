package com.ral.young.concurrent.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * {@link java.util.concurrent.locks.ReentrantReadWriteLock}
 * <p>
 * 读写锁：ReentrantReadWriteLock
 *
 * @author renyunhui
 * @date 2022-11-03 11:08
 * @since 1.0.0
 */
@Slf4j
public class ReentrantReadWriteLockTest {

    private static final ReentrantReadWriteLock REENTRANT_READ_WRITE_LOCK = new ReentrantReadWriteLock();

    private Object data;

    private static class SingleFactory {
        // 静态内部类实现单例模式
        private static final ReentrantReadWriteLockTest INSTANCE = new ReentrantReadWriteLockTest();
    }

    public static ReentrantReadWriteLockTest getInstance() {
        return SingleFactory.INSTANCE;
    }

    public static void main(String[] args) throws InterruptedException {
        /*
         * 1.支持公平与非公平的获取锁方式。
         * 2.支持可重入，读线程获取读锁后还可以获取读锁，但是不能获取写锁；写线程获取写锁后既可以再次获取写锁还可以获取读锁。
         * 3.允许从写锁降级为读锁，其实现方式是：先获取写锁，然后获取读锁，最后释放写锁。但是，从读锁升级到写锁是不可以的；
         * 4.读取锁和写入锁都支持锁获取期间的中断；
         * 5.Condition支持。仅写入锁提供了一个 Condition 实现；读取锁不支持 Condition ，readLock().newCondition() 会抛出 UnsupportedOperationException。
         */
        ReentrantReadWriteLockTest reentrantReadWriteLockTest = getInstance();
        for (int i = 0, size = 10; i < size; i++) {
            new Thread(reentrantReadWriteLockTest::read).start();
            new Thread(() -> reentrantReadWriteLockTest.write(ThreadLocalRandom.current().nextInt(1, 20) + "_商品")).start();
        }

        Thread.sleep(10000);
        log.info("主线程执行完毕");
    }

    public void read() {
        // 上读锁 - 读锁是共享的,同一时间可以支持多线程进行读锁的获取
        // 读锁不可以升级为写锁
        REENTRANT_READ_WRITE_LOCK.readLock().lock();

        try {
            log.info("{},获取到的读锁,准备读取数据", Thread.currentThread().getName());
            Thread.sleep(200);
            log.info("{},读取数据完毕,读取到的数据为:{}", Thread.currentThread().getName(), data);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            REENTRANT_READ_WRITE_LOCK.readLock().unlock();
        }
    }

    public void write(Object data) {
        // 上写锁 - 同一时间只有一个线程可以获取到写锁,写锁是互斥的
        // 写锁可以降级为读锁
        REENTRANT_READ_WRITE_LOCK.writeLock().lock();

        try {
            log.info("{},获取到的写锁,准备写入数据", Thread.currentThread().getName());
            this.data = data;
            Thread.sleep(600);
            log.info("{},写入数据完毕", Thread.currentThread().getName());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            REENTRANT_READ_WRITE_LOCK.writeLock().unlock();
        }
    }
}
