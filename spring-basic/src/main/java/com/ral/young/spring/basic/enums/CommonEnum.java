package com.ral.young.spring.basic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

public class CommonEnum {

    public enum GenderEnum {
        MALE(1, "男"), FEMALE(2, "女"), ELSE(3, "其它");

        @EnumValue
        private final int code;

        @Getter
        private final String description;

        GenderEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getValue() {
            return this.code;
        }
    }

}