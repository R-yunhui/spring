package com.ral.young.enums;

/**
 * @author renyunhui
 * @description 这是一个Demo类
 * @date 2024-08-28 10-47-22
 * @since 1.0.0
 */
public class Demo {

    public static void main(String[] args) {
        TimeEnum nearlyTwoDays = TimeEnum.valueOf("NEARLY_TWO_DAYS");
        nearlyTwoDays.getNearlyTime();
    }
}
