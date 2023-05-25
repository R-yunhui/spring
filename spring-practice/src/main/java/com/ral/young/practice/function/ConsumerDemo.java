package com.ral.young.practice.function;

import cn.hutool.core.util.IdUtil;

import java.util.function.Consumer;

/**
 * {@link java.util.function.Consumer}
 *
 * java.util.function.Consumer<T>接口与Supplier接口相反，被称之为消费型接口，它不是生产一个数据，而是消费一个数据，其数据类型由指定的泛型决定
 *
 * 该接口包含抽象方法void accept(T t)，作用是消费一个指定泛型的数据，消费是自定义的(输出、计算....)
 *
 * @author renyunhui
 * @date 2023-05-25 14:12
 * @since 1.0.0
 */
public class ConsumerDemo {

    int sum = 0;

    public static void main(String[] args) {
        ConsumerDemo demo = new ConsumerDemo();
        String name = IdUtil.fastSimpleUUID();
        // 对给定参数执行定义的函数操作
        demo.testConsumerOne(o -> demo.functionOne(name), name);

        int a = 10;
        demo.testConsumerTwo(o -> demo.functionTwo(a), Integer -> demo.functionThree(a), a);
    }

    public void testConsumerOne(Consumer<String> consumer, String name) {
        consumer.accept(name);
    }

    public void functionOne(String name) {
        System.out.println(name);
    }

    public void testConsumerTwo(Consumer<Integer> consumerOne, Consumer<Integer> consumerTwo, int a) {
        // 返回一个 consumer 结果是：先调用前一个 consumer - functionTwo 在调用后一个 consumer - functionThree
        Consumer<Integer> consumer = consumerOne.andThen(consumerTwo);

        // consumer 就是 两个函数的组合，然后在进行组合处理
        consumer.accept(a);
    }

    public void functionTwo(int a) {
        sum += a;
        System.out.println("functionTwo:" + sum);
    }

    public void functionThree(int a) {
        sum *= a;
        System.out.println("functionThree:" + sum);
    }
}
