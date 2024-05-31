package com.ral.young.night.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池拒绝策略
 *
 * @author renyunhui
 * @date 2024-05-28 10:55
 * @since 1.0.0
 */
public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println(Thread.currentThread().getName() + " 拒绝当前任务的执行，当前任务队列中的任务数量：" + executor.getQueue().size());
    }
}
