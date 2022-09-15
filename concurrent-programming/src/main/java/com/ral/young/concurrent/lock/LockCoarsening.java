package com.ral.young.concurrent.lock;

/**
 * jdk1.6 之后对于 synchronized 的优化，锁粗化
 * 依赖逃逸分析
 *
 * @author renyunhui
 * @date 2022-09-14 17:23
 * @since 1.0.0
 */
public class LockCoarsening {

    public static void main(String[] args) {
        Object o = new Object();

        // 类似这种情况，会将多把锁的锁逻辑，变为一把锁将逻辑锁起来 - 锁粗化
        synchronized (o) {
            System.out.println();
        }

        synchronized (o) {
            System.out.println();
        }

        synchronized (o) {
            System.out.println();
        }

        synchronized (o) {
            System.out.println();
        }
    }
}
