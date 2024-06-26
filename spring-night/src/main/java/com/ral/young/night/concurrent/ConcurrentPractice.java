package com.ral.young.night.concurrent;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author renyunhui
 * @date 2024-06-26 16:23
 * @since 1.0.0
 */
@Slf4j
public class ConcurrentPractice {

    public static void main(String[] args) {
        // testOne();

        // testTwo();

        // testThree();

        testFour();
    }

    private static final Map<String, Long> raceResult = new HashMap<>(16);

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> {
        log.info("所有马到达终点后裁判宣布赛马成绩");
        raceResult.forEach((k, v) -> {
            log.info("{} 跑完了，用时：{} 毫秒", k, v);
        });
    });

    private static final int MAX_VALUE = 100;

    private static int CUR_VALUE = 1;

    private static boolean printNumber = true;

    private static final int MAX_SIZE = 10;

    private static int CUR_SIZE = 1;

    private static final Object lock = new Object();

    private static final Object lockTwo = new Object();

    /**
     * 10个线程模拟赛马，所有马就绪后才能开跑，所有马到达终点后裁判宣布赛马成绩
     */
    public static void testOne() {
        for (int i = 0; i < 10; i++) {
            new Thread(new RaceHorse()).start();
        }

        log.info("所有马已经准备就绪，开始赛跑");
        // state == 0 才能继续执行，唤醒被 await 操作阻塞的线程
        countDownLatch.countDown();
    }

    /**
     * 两个线程，一个打印奇数，一个打印偶数，然后顺序打印出1-100
     */
    public static void testTwo() {
        Thread evenThread = new Thread(() -> {
            while (CUR_VALUE <= MAX_VALUE) {
                synchronized (lock) {
                    if ((CUR_VALUE & 1) == 0) {
                        // 偶数
                        System.out.print(CUR_VALUE + " ");
                        CUR_VALUE++;
                        // 唤醒奇数打印线程
                        lock.notify();
                    } else {
                        try {
                            // 奇数则阻塞在这里，等待偶数打印线程唤醒在尝试获取锁判断是否需要执行业务操作
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        Thread oddThread = new Thread(() -> {
            while (CUR_VALUE <= MAX_VALUE) {
                synchronized (lock) {
                    if ((CUR_VALUE & 1) != 0) {
                        // 奇数
                        System.out.print(CUR_VALUE + " ");
                        CUR_VALUE++;
                        // 唤醒偶数打印线程
                        lock.notify();
                    } else {
                        try {
                            // 偶数则阻塞在这里，等待奇数打印线程唤醒在尝试获取锁判断是否需要执行业务操作
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        oddThread.start();
        evenThread.start();
    }

    /**
     * 两个线程，一个打印 123，一个打印 ABC，交替输出1A2B3C
     */
    public static void testThree() {
        Thread printNumberThread = new Thread(() -> {
            while (true) {
                synchronized (lockTwo) {
                    if (CUR_SIZE <= MAX_SIZE) {
                        for (int i = 1; i <= 3; i++) {
                            while (!printNumber) {
                                // 不是打印数字，则阻塞等待唤醒
                                try {
                                    lockTwo.wait();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            if (CUR_SIZE > MAX_SIZE) {
                                return;
                            }

                            System.out.print(i + " ");
                            // 唤醒打印字母的线程，修改 printNumber = false;
                            printNumber = false;
                            lockTwo.notify();
                        }

                    }
                }
            }
        });

        Thread printLetterThread = new Thread(() -> {
            while (true) {
                synchronized (lockTwo) {
                    if (CUR_SIZE <= MAX_SIZE) {
                        for (char c = 'A'; c <= 'C'; c++) {
                            while (printNumber) {
                                // 打印数字，则阻塞等待唤醒
                                try {
                                    lockTwo.wait();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            if (CUR_SIZE > MAX_SIZE) {
                                return;
                            }

                            System.out.print(c + " ");
                            // 唤醒打印数字的线程，修改 printNumber = true;
                            printNumber = true;
                            lockTwo.notify();
                        }

                        System.out.println("当前循环次数：" + CUR_SIZE);
                        CUR_SIZE++;
                    }
                }
            }
        });

        printLetterThread.start();
        printNumberThread.start();
    }

    /**
     * 并发调多个方法，实现只要有一个成功就立即成功，否则等都失败才失败
     */
    public static void testFour() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1024),
                ThreadFactoryBuilder.create().setNamePrefix("test-four-").build());

        // CompletionService的实现目标是任务先完成可优先获取到，即结果按照完成先后顺序排序。
        CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);
        int size = 100;
        boolean success = false;
        IntStream.range(0, size).forEach(o -> completionService.submit(new TaskOne()));
        for (int i = 0; i < size; i++) {
            try {
                // 获取结果
                Future<Boolean> future = completionService.take();
                // 获取结果
                Boolean result = future.get();
                // 只要有一个成功就返回成功
                if (result) {
                    success = true;
                    break;
                }
            } catch (Exception e) {
                log.error("执行任务异常：", e);
            }
        }

        System.out.println("执行结果：" + success);
    }

    static class RaceHorse implements Runnable {

        @Override
        public void run() {
            try {
                // 等待所有马就绪才能开始跑，使用 countDownLatch
                log.info("{} 等待所有马匹就绪再开始赛跑", Thread.currentThread().getName());
                // state != 0 会一直阻塞在这里
                countDownLatch.await();

                long raceTime = (long) (Math.random() * 500) + 1000;
                // 模拟赛跑
                Thread.sleep(raceTime);

                raceResult.put(Thread.currentThread().getName(), raceTime);
                // 没有减到 0，会一直阻塞在这里，直到减到 0，才会继续往后执行
                cyclicBarrier.await();
            } catch (Exception e) {
                log.error("赛马执行异常：", e);
            }
        }
    }

    @Setter
    @Getter
    static class TaskOne implements Callable<Boolean> {

        private boolean success;

        @Override
        public Boolean call() throws Exception {
            long dealTime = (long) (Math.random() * 1000) + 1000;
            try {
                Thread.sleep(dealTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int random = (int) (Math.random() * 10);
            this.success = random <= 1;
            log.info("{} 执行完成，执行结果：{}", Thread.currentThread().getName(), success);
            return success;
        }
    }
}
