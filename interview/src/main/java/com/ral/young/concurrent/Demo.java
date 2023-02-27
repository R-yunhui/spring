package com.ral.young.concurrent;

/**
 *
 * @author renyunhui
 * @date 2023-02-16 20:00
 * @since 1.0.0
 */
public class Demo {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0, n = 10000000; i < n; i++) {
            // doSomeThing

            if (i % 1000 == 0) {
                // 使用 native 方法，可以让这里进入 safe region，不阻塞 gc 的执行
                Thread.sleep(0);
            }
        }
    }
}
