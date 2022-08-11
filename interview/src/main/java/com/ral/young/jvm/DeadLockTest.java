package com.ral.young.jvm;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 死锁检测
 *
 * @author renyunhui
 * @date 2022-08-01 10:12
 * @since 1.0.0
 */
@Slf4j
public class DeadLockTest {

    private static Object lockOne = new Object();

    private static Object lockTwo = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (lockOne) {
                try {
                    log.info("{}运行中,锁对象:{},时间:{}", Thread.currentThread().getName(), lockOne, DateUtil.now());

                    Thread.sleep(5000);

                    synchronized (lockTwo) {
                        log.info("{}运行中,锁对象:{},时间:{}", Thread.currentThread().getName(), lockTwo, DateUtil.now());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (lockTwo) {
                try {
                    log.info("{}运行中,锁对象:{},时间:{}", Thread.currentThread().getName(), lockTwo, DateUtil.now());

                    Thread.sleep(5000);

                    synchronized (lockOne) {
                        log.info("{}运行中,锁对象:{},时间:{}", Thread.currentThread().getName(), lockOne, DateUtil.now());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
