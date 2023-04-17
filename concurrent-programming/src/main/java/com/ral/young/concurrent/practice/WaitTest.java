package com.ral.young.concurrent.practice;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * wait 方法为什么必须定义在循环体内？
 *
 * @author renyunhui
 * @date 2023-04-13 14:36
 * @since 1.0.0
 */
public class WaitTest {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(11, 11, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));

    public static void main(String[] args) {
        BufferOne bufferOne = new BufferOne();
        int size = 10;
        THREAD_POOL_EXECUTOR.execute(() -> {
            while (true) {
                try {
                    bufferOne.put(ThreadLocalRandom.current().nextInt());
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        for (int i = 0; i < size; i++) {
            THREAD_POOL_EXECUTOR.execute(() -> {
                while (true) {
                    try {
                        int i1 = bufferOne.get();
                        System.out.println(i1);
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        THREAD_POOL_EXECUTOR.shutdown();
    }

    static class BufferOne {
        private final List<Integer> list = new ArrayList<>();

        synchronized void put(int v) throws InterruptedException {
            int MAX = 5;
            if (list.size() == MAX) {
                wait();
            }
            list.add(v);
            notifyAll();
        }

        synchronized int get() throws InterruptedException {
            /*
             * 会导致数组下标越界
             * 假设线程 A 先获取锁，获取不到数据阻塞，释放了锁，线程 B 同理也被阻塞（多线程的情况下）
             * 线程 C 获取到锁，添加了数据，调用了 notifyAll 导致线程 B 和 线程 A 都被唤醒，去移除数据导致出现了数组下标越界
             *
             * 需要将 wait 操作放到循环体内部执行，保证 wait 的线程是满足条件才继续执行
             */
            if (list.size() == 0) {
                wait();
            }
            int v = list.remove(0);
            notifyAll();
            return v;
        }

        synchronized int size() {
            return list.size();
        }
    }
}
