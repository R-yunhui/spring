package com.ral.young.jvm;

import lombok.extern.slf4j.Slf4j;

/**
 * 栈内存溢出
 *
 * @author renyunhui
 * @date 2022-07-28 9:31
 * @since 1.0.0
 */
@Slf4j
public class StackOverFlowTest {

    private static int count;

    public static void main(String[] args) {
        StackOverFlowTest stackOverFlowTest = new StackOverFlowTest();
        try {
            stackOverFlowTest.recursion();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(count);
        }
    }

    public void recursion() {
        count++;
        recursion();
    }
}
