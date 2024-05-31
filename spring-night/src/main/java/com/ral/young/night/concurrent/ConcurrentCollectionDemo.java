package com.ral.young.night.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 并发集合示例
 *
 * @author renyunhui
 * @date 2024-05-31 14:35
 * @since 1.0.0
 */
public class ConcurrentCollectionDemo {

    public static void main(String[] args) {

    }

    public static void testArrayBlockingQueue() {
        /*
         *  必须指定容量
         *  默认使用的是非公平锁 nonFair
         *  内部使用了  ReentrantLock 以及 Condition 条件变量（notEmpty notFull），分别对应两个不同的条件队列
         */
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        // add() 添加元素，如果队列已满，则抛出IllegalStateException异常，也是调用 offer，如果添加不成功，会抛出异常
        queue.add(1);

        // offer() 添加元素，如果队列已满，则返回false
        queue.offer(1);

        // offer(E e, long timeout, TimeUnit unit) 添加元素，如果队列已满，则阻塞，直到超时返回false，会抛出中断异常
        try {
            queue.offer(1, 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // put() 添加元素，如果队列已满，则阻塞，会抛出中断异常
        try {
            queue.put(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // poll() 移除并返回队列头部的元素，如果队列为空，则返回null
        queue.poll();

        // poll(long timeout, TimeUnit unit) 移除并返回队列头部的元素，如果队列为空，则阻塞，直到超时返回null，会抛出中断异常
        try {
            queue.poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // remove() 移除并返回队列头部的元素，如果队列为空，则抛出NoSuchElementException异常
        queue.remove();

        // take() 移除并返回队列头部的元素，如果队列为空，则阻塞，会抛出中断异常
        try {
            queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testLinkedBlockingQueue() {
        // 默认容量：Integer.MAX_VALUE
        // 只有非公平锁的实现方式，内部维持了两把锁 putLock and takeLock
        // 以及对应的两个条件变量：putLock.notEmpty and takeLock.notFull
        // 相比于ArrayBlockingQueue，LinkedBlockingQueue的容量是无限的，并且读写支持的并发度高，但是性能比ArrayBlockingQueue低
        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

        // 读写的api 和 ArrayBlockingQueue一样
    }

    public static void testConcurrentHashMap() {
        // 默认容量为 16，在 put 数据的时候进行初始化 table 的动作，懒加载的（只有一个线程可以执行 table 的初始化动作）
        // 使用 Synchronized + CAS 的方式保证线程安全
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();

        // 在出现哈希冲突的时候会加锁，加锁的范围只是单个 Node 节点，相比于 1.7 锁 Segement 的粒度小很多
        concurrentHashMap.put("name", "mike");

    }
}
