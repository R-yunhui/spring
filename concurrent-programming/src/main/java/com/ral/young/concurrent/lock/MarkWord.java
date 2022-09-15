package com.ral.young.concurrent.lock;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * 加锁之后对象头的 mark word
 *
 * @author renyunhui
 * @date 2022-09-14 13:52
 * @since 1.0.0
 */
@Slf4j
public class MarkWord {

    public static void main(String[] args) throws InterruptedException {
        // 由于 jvm 默认会会延迟启动偏向锁，再次睡一段时间验证偏向锁的 mark word
        Thread.sleep(5000);

        Object o = new Object();
        // 会存在匿名偏向，可偏向的一种状态，mark word 已经是 01，但是并没有偏向线程 id 的。说明可以加偏向锁，但是还没有偏向任何线程。
        log.info("匿名偏向:{}", ClassLayout.parseInstance(o).toPrintable());

        // 偏向锁
        new Thread(() -> {
            synchronized (o) {
                log.info("偏向锁:{}", ClassLayout.parseInstance(o).toPrintable());
            }
        }).start();

        Thread.sleep(2000);

        log.info("偏向锁:{}", ClassLayout.parseInstance(o).toPrintable());
        // 轻量级锁，满足多个线程交替执行
        new Thread(() -> {
            synchronized (o) {
                log.info("轻量级锁:{}", ClassLayout.parseInstance(o).toPrintable());
            }
        }).start();

        // 重量级锁
        for (int i = 0, size = 2; i < size; i++) {
            // 多个线程之间存在竞争
            new Thread(() -> {
                synchronized (o) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    log.info("重量级锁:{}", ClassLayout.parseInstance(o).toPrintable());
                }
            }).start();
        }
    }
}
