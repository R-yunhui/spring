package com.ral.young.concurrent.lock;

/**
 * jdk1.6 之后对于 synchronized 的优化，锁消除
 *
 * @author renyunhui
 * @date 2022-09-14 17:26
 * @since 1.0.0
 */
public class LockRemoval {

    public static void main(String[] args) {
        LockRemoval lockRemoval = new LockRemoval();
        lockRemoval.method();
    }

    private void method() {
        // 依赖逃逸分析 - 进行锁消除
        // o 指向的对象只能被一个线程所持有，所以不存在竞争的关系，可以进行锁消除
        Object o = new Object();

        synchronized (o) {
            System.out.println();
        }
    }
}
