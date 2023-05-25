package com.ral.young.practice.function;

import java.util.function.Function;

/**
 * {@link java.util.function.Function}
 *
 * java.util.function.Function<T,R> 接口为一个转换类型的接口，用来根据一个类型的数据得到另一个类型的数据，T称为前置条件，
 *
 * R称为后置条件
 *
 * @author renyunhui
 * @date 2023-05-25 14:47
 * @since 1.0.0
 */
public class FunctionDemo {

    public static void main(String[] args) {
        FunctionDemo demo = new FunctionDemo();
        demo.testFunctionOne(o -> Integer.parseInt(o) * 10, "11");

        demo.testFunctionTwo(o1 -> Integer.parseInt(o1) * 3, o2 -> o2 + "222", 111);

        demo.testFunctionThree(o1 -> Integer.parseInt(o1) * 3, o2 -> o2 + "aaa", "111");
    }

    public void testFunctionOne(Function<String, Integer> function, String data) {
        // 通过指定的函数来计算 T 得到结果 R
        Integer result = function.apply(data);
        System.out.println("testFunctionOne result:" + result);
    }

    public void testFunctionTwo(Function<String, Integer> functionOne, Function<Integer, String> functionTwo, int data) {
        // 返回一个组合函数，该函数首先将 before 函数应用于其输入，然后将此函数应用于结果。如果任一函数的计算引发异常，则会将其中继到组合函数的调用方。
        // 将后者接口的计算结果应用到前者的参数中参与后者接口的计算
        Integer result = functionOne.compose(functionTwo).apply(data);
        System.out.println("testFunctionTwo result:" + result);
    }

    public void testFunctionThree(Function<String, Integer> functionOne, Function<Integer, String> functionTwo, String data) {
        // 返回一个组合函数，该函数首先将此函数应用于其输入，然后将该 after 函数应用于结果。如果任一函数的计算引发异常，则会将其中继到组合函数的调用方。
        // 将前者接口的计算结果应用到后者的参数中参与后者接口的计算
        String apply = functionOne.andThen(functionTwo).apply(data);
        System.out.println("testFunctionThree result:" + apply);
    }
}
