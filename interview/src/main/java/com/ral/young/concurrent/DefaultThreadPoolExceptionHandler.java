package com.ral.young.concurrent;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link Thread.UncaughtExceptionHandler}
 *
 * @author renyunhui
 * @date 2023-02-09 18:53
 * @since 1.0.0
 */
@Slf4j
public class DefaultThreadPoolExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("线程:{} 执行任务异常,errorMsg:{}", t.getName(), e.getMessage(), e);
        // 可以进一步完善，把需要记住的异常信息存入指定的地方
    }
}
