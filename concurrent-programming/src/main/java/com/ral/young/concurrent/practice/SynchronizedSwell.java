package com.ral.young.concurrent.practice;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * Synchronized 锁膨胀升级
 *
 * @author renyunhui
 * @date 2023-04-12 16:41
 * @since 1.0.0
 */
@Slf4j
public class SynchronizedSwell {

    public static void main(String[] args) throws InterruptedException {
        // 睡眠 5s
        Thread.sleep(5000);
        Object o = new Object();
        // 初始可偏向状态 - biasable
        log.info("未进入同步块，MarkWord 为：");
        log.info(ClassLayout.parseInstance(o).toPrintable());

        synchronized (o) {
            // 偏向锁：biased - 偏向线程id
            log.info(("进入同步块，MarkWord 为："));
            log.info(ClassLayout.parseInstance(o).toPrintable());
        }

        log.info(("退出同步块，MarkWord 为："));
        log.info(ClassLayout.parseInstance(o).toPrintable());

        Thread t2 = new Thread(() -> {
            synchronized (o) {
                // 升级为轻量级锁 - thin
                log.info("新线程获取锁，MarkWord为：");
                log.info(ClassLayout.parseInstance(o).toPrintable());
            }
        });

        t2.start();
        t2.join();
        // 不可偏向状态 - non-biasable
        log.info("主线程再次查看锁对象，MarkWord为：");
        log.info(ClassLayout.parseInstance(o).toPrintable());

        synchronized (o) {
            // 只要锁升级成为了轻量级锁，就不会降级 - thin
            log.info(("主线程再次进入同步块，MarkWord 为："));
            log.info(ClassLayout.parseInstance(o).toPrintable());
        }
    }
}
