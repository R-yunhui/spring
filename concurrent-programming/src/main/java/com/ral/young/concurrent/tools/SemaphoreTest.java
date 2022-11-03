package com.ral.young.concurrent.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * {@link java.util.concurrent.Semaphore}
 * 限制访问某一特定资源的线程数的并发工具类，线程间通信的工具
 *
 * @author renyunhui
 * @date 2022-09-21 17:40
 * @since 1.0.0
 */
@Slf4j
public class SemaphoreTest {

    /**
     * 初始化同步状态 state = 3，总的许可数量 = 3
     */
    static Semaphore semaphore = new Semaphore(3);

    public static void main(String[] args) throws InterruptedException {
        int size = 10;
        for (int i = 0; i < size; i++) {
            new Thread(SemaphoreTest::task).start();
        }

        TimeUnit.MINUTES.sleep(1);

        log.info("主线程执行");
    }

    private static void task() {
        try {
            // 获取一个许可
            // state = 3 - 1
            // 使用此方法获取许可，如果线程被中断，会抛出异常
            semaphore.acquire();
            log.info("{} 或取许可", Thread.currentThread().getName());
            Thread.sleep(5000);

            // 释放一个令牌
            semaphore.release();
            log.info("{} 释放许可", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            log.error("线程中断异常,线程名称:{},errorMsg:{}", Thread.currentThread().getName(), e.getMessage(), e);
        }
    }
}
