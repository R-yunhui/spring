package com.ral.young.jvm;

/**
 * 测试 cpu 飙升
 *
 * @author renyunhui
 * @date 2022-08-01 11:01
 * @since 1.0.0
 */
public class Math {

    private static final User INIT_USER = new User();

    private static final int INIT_ID = 33;

    public static void main(String[] args) {
        Math math = new Math();
        while (true) {
            math.compute();
        }
    }

    private int compute() {
        int a = 1;
        int b = 2;
        int c = (a + b) * 10;
        return c;
    }
}
