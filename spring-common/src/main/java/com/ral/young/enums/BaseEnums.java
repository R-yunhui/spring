package com.ral.young.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 基础枚举
 *
 * @author renyunhui
 * @date 2023-06-20 13:47
 * @since 1.0.0
 */
public class BaseEnums {

    public static enum GenderEnum implements IEnum<Integer> {
        MAN(0, "男性"),

        WOMAN(1, "女性"),

        ELSE(2, "其它");

        /**
         * 注解 @EnumValue 标记在数据库中存储的数据
         */
        @EnumValue
        private final Integer code;

        /**
         * 注解 @EnumValue 标记在响应的json数据
         */
        @JsonValue
        private final String desc;

        GenderEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        @Override
        public Integer getValue() {
            return code;
        }
    }
}
