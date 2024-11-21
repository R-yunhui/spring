package com.ral.young.spring.basic.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public class CommonEnum {

    @Getter
    public enum GenderEnum implements IEnum<Integer> {

        MALE(1, "男"),

        FEMALE(2, "女"),

        UNKNOWN(0, "未知");

        @JsonValue
        private final Integer value;
        private final String desc;

        GenderEnum(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        @Override
        public Integer getValue() {
            return this.value;
        }

        public static GenderEnum fromValue(Integer value) {
            if (value == null) {
                return null;
            }
            for (GenderEnum gender : GenderEnum.values()) {
                if (gender.value.equals(value)) {
                    return gender;
                }
            }
            return UNKNOWN;
        }
    }
}