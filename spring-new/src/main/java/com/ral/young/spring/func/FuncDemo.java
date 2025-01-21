package com.ral.young.spring.func;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author renyunhui
 * @description 这是一个FuncDemo类
 * @date 2025-01-16 09-52-00
 * @since 1.0.0
 */
public class FuncDemo {

    public static void main(String[] args) {
        testConsumerFunc(data -> {
            data = data + " World";
            System.out.println(data);
        }, "Haha");

        testConsumerFuncTwo(data -> {
            data += " ConsumerOne";
            System.out.println(data);
        }, "Hello", data -> {
            data += " ConsumerTwo";
            System.out.println(data);
        });

        int len = testFuncOne((s) -> {
            int l = s.length();
            return l + 3;
        }, "Hello Word");
        System.out.println("testFuncOne result: " + len);
    }

    public static void testConsumerFunc(Consumer<String> consumer, String data) {
        consumer.accept(data);
    }

    public static void testConsumerFuncTwo(Consumer<String> consumerOne, String dataOne, Consumer<String> consumerTwo) {
        // 直接返回一个 Consumer，顺序执行两个函数
        Consumer<String> stringConsumer = consumerOne.andThen(consumerTwo);
        stringConsumer.accept(dataOne);
    }

    public static int testFuncOne(Function<String, Integer> function, String data) {
        return function.apply(data);
    }
}
