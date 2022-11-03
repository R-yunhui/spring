package com.ral.young.bytecode;

/**
 *
 * @author renyunhui
 * @date 2022-11-02 11:23
 * @since 1.0.0
 */
public class SynchronizedDemo {

    private static Object o = new Object();

    public static void main(String[] args) {
        int a = 3;
        synchronized (o) {
            a = 4;
            int b = 5;
        }
    }
}
