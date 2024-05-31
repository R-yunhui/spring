package com.ral.young.night.concurrent;

/**
 * 自定义线程异常处理
 *
 * @author renyunhui
 * @date 2024-05-28 9:53
 * @since 1.0.0
 */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(Thread.currentThread().getName() + " 捕获到：" + t.getName() + "，执行异常，异常信息：" + e);
    }
}
