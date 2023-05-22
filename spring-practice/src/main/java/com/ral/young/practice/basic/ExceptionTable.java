package com.ral.young.practice.basic;

import lombok.extern.slf4j.Slf4j;

/**
 * jvm 如何处理异常
 *
 * @author renyunhui
 * @date 2023-04-17 10:11
 * @since 1.0.0
 */
@Slf4j
public class ExceptionTable {

    public static void main(String[] args) {
        testA();
    }

    /**
     * 1.jvm 通过异常表 exception table 处理异常
     * 2.如下代码会生成4个 exception table 分别处理 InterruptedException，Exception，any，any
     * 3.前面的两个 exception table 分别对应 catch 代码块捕获的异常，后面的对应处理 finally 的，分别处理 catch 不匹配的异常以及 catch 内部报错的情况
     * 4.当程序触发异常时，JVM会从上到下遍历异常表中的所有条目，当触发异常的字节码索引值在某个异常表条目的监控范围内，JVM会判断所抛出的异常是否和该条目要捕获的异常是否匹配。
     *   如果匹配，JVM会将控制流转移到该条目target指针指向的字节码。如果遍历完当前方法的所有异常表条目，都没有匹配到异常处理器，那么会弹出当前方法对应的Java栈帧，并且重复上述操作。
     * 5.exception table 的 from 指针和 to 指针标示了该异常处理器所监控的范围，例如 try 代码块所覆盖的范围。target 指针则指向异常处理器的起始位置，例如 catch 代码块的起始位置。type 表示监控的异常类型
     */
    public static void testA() {
        log.info("开始执行任务");
        try {
            int a = 3;
            int b = 4;
            log.info("进入 try 代码块");
            Thread.sleep(1000);
            log.info("a + b:{}", (a + b));
        } catch (InterruptedException e1) {
            log.error("处理 InterruptedException 异常");
        } catch (Exception e) {
            log.error("处理 Exception 异常");
        } finally {
            log.info("执行 finally 代码块");
        }
    }
}
