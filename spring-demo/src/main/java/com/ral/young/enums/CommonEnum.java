package com.ral.young.enums;

import lombok.Getter;

/**
 * @author renyunhui
 * @description 这是一个CommonEnum类
 * @date 2024-09-11 10-08-18
 * @since 1.0.0
 */
public class CommonEnum {

    @Getter
    public static enum GnderEnum {

        MALE(1, "男"),

        FEMALE(2, "女");

        private int code;

        private String val;

        GnderEnum(int code, String val) {
        }
    }
}
