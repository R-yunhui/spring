package com.ral.young.basic.concurrent;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.ArrayBlockingQueue}
 *
 * @author renyunhui
 * @date 2023-05-30 10:13
 * @since 1.0.0
 */
public class ArrayBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        /*
         * 1.内部使用了一个 ReentrantLock 在读写的时候进行加锁
         * 2.使用了两个 Condition notFull - 当前队列容量为空的时候，阻塞写入队列的线程，以及 notEmpty - 当队列容量为 0 时，阻塞读取当前队列数据的线程
         * 3.当元素入队时，则会唤醒阻塞在 notEmpty 的条件等待队列的线程，添加到 ReentrantLock 的 CLH 队列中尝试拿锁获取数据
         * 4.当元素出队时，则会唤醒阻塞在 notFull 的条件等待队列的线程，添加到 ReentrantLock 的 CLH 队列中尝试拿锁获取数据
         *
         * 只使用了一把锁，读写同步都依赖这一把锁，相比于 LinkedBlockingQueue 来说并发度较低，
         *
         * LinkedBlockingQueue 内部使用了两把锁，分别在读操作和写操作的时候进行加锁，提高了并发度
         *
         * ----------------------
         * offer：添加失败返回 false，添加成功返回 true
         * offer(E e, long timeout, TimeUnit unit)：规定时间内添加失败返回 false，添加成功返回 true，没到规定的时间会阻塞
         *
         * add：调用的就是 offer，添加成功返回 true，添加失败抛出异常：队列已满
         *
         * put：队列满了则会阻塞
         *
         * ----------------------
         * take：队列为空则会阻塞
         *
         * poll：成功则会返回对应的元素，失败则返回 null
         * poll(long timeout, TimeUnit unit)：队列为空则会阻塞指定的时间，成功则会返回对应的元素，失败则返回 null
         */
        ArrayBlockingQueueDemo demo = new ArrayBlockingQueueDemo();
        demo.testArrayBlockingQueue();
    }

    private static final int MAX_SIZE = 100;

    private static final int PRODUCT_NUM = 5;

    private static final int CONSUMER_NUM = 10;

    private final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(MAX_SIZE);

    private void testArrayBlockingQueue() throws InterruptedException {
        for (int i = 0; i < PRODUCT_NUM; i++) {
            Thread t1 = new Thread(() -> {
                try {
                    // 队列满了会进行阻塞 30 ms
                    queue.offer(IdUtil.fastSimpleUUID(), 30, TimeUnit.MILLISECONDS);
                    queue.offer(IdUtil.fastSimpleUUID(), 30, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(Thread.currentThread().getName() + "：生产完成,当前药品数量：" + queue.size());
            });

            t1.start();
        }

        for (int i = 0; i < CONSUMER_NUM; i++) {
            Thread t1 = new Thread(() -> {
                String poll;
                try {
                    // 会阻塞在这里 100ms 等待队列中有数据会被唤醒及时进行消费，反正消费失败数据为 null
                    poll = queue.poll(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (StrUtil.isBlank(poll)) {
                    System.out.println(Thread.currentThread().getName() + "：在 100 ms内消费失败,当前剩余的药品数量：" + queue.size());
                } else {
                    System.out.println(Thread.currentThread().getName() + "：消费完成,当前剩余的药品数量：" + queue.size());
                }
            });

            t1.start();
        }

        Thread.sleep(3000);
        System.out.println("仓库剩余的药品数量：" + queue.size());
    }
}
