package com.ral.young.bytecode;

/**
 * @author renyunhui
 * @date 2022-11-02 10:45
 * @since 1.0.0
 */
public class ByteCodeDemo {

    public static void main(String[] args) {
        add(1, 1);
    }

    public static int add(int a, int b) {
        int c = 0;
        return a + b;
    }
}
