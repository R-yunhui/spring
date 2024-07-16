package com.ral.young.night.concurrent;

/**
 * 线程的生命周期
 *
 * @author renyunhui
 * @date 2024-07-16 13:51
 * @since 1.0.0
 */
public class ThreadDemo {

    public static void main(String[] args) {
        /*
         * 1.新建 NEW
         * 2.就绪 RUNNABLE
         * 3.允许 RUNNING
         * 4.阻塞 BLOCKED
         * 5.死亡 DEAD
         *
         * 阻塞分为三种：
         * 1.同步阻塞：通过加锁的方式，Synchronized，运行的线程在获取对象的同步锁的时候，如果该对象的同步锁被其它线程所持有，则 JVM 会把该线程放到锁池中
         * 2.等待阻塞：运行的线程执行wait方法，该线程会释放占用的所有资源，JVM会把该线程放入“等待池”中。进入这个状态后，是不能被自动唤醒的，必须依靠其他线程调用notify或notifyAll方法才能被唤醒，wait是object类的方法。
         * 3.其它阻塞：运行的线程执行sleep或join方法，或者发出了I/O请求时，JVM会把该线程置为阻塞状态。当sleep状态超时、join等待线程终止或超时、或者I/O处理完毕时，线程重新转入就绪状态。sleep是Thread类的方法。
         */
    }
}
