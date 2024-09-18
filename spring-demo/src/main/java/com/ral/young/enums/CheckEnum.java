package com.ral.young.enums;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author renyunhui
 * @description 这是一个CheckEnum类
 * @date 2024-09-11 11-14-50
 * @since 1.0.0
 */
public enum CheckEnum implements Predicate<Object> {

    CHECK_NULL(Objects::isNull),
    CHECK_NOT_NULL(Objects::nonNull),
    CHECK_NOT_BLANK(o -> o != null && !o.toString().trim().isEmpty()),
    CHECK_BLANK(o -> o == null || o.toString().trim().isEmpty()),
    CHECK_NOT_EMPTY(o -> o != null && !o.toString().isEmpty()),
    CHECK_EMPTY(o -> o == null || o.toString().isEmpty()),
    CHECK_NOT_EQUAL(Objects::nonNull),
    ;

    CheckEnum(Predicate<Object> predicate) {
    }

    @Override
    public boolean test(Object o) {
        return false;
    }
}
