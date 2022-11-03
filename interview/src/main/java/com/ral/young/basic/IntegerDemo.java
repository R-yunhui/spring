package com.ral.young.basic;

/**
 * {@link Integer}
 *
 * @author renyunhui
 * @date 2022-10-17 14:26
 * @since 1.0.0
 */
public class IntegerDemo {

    public static void main(String[] args) {
        Integer i1 = 100;
        Integer i2 = 100;
        Integer i3 = 200;
        Integer i4 = 200;

        // true
        System.out.println(i1 == i2);

        // false
        System.out.println(i3 == i4);
    }
}
