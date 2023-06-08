package com.ral.young.mybatis.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 性别枚举
 *
 * @author renyunhui
 * @date 2023-06-08 14:19
 * @since 1.0.0
 */
public enum SexEnum implements IEnum<Integer> {
    MAN(1, "男"), WOMAN(2, "女"), ELSE(3, "其它"),
    ;

    /**
     * 注解 @EnumValue 标记在数据库中存储的数据
     */
    @EnumValue
    private final Integer code;

    /**
     * 注解 @EnumValue 标记在响应的json数据
     */
    @JsonValue
    private final String name;

    SexEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
