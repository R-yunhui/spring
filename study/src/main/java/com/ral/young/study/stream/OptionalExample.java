package com.ral.young.study.stream;

import java.util.Optional;

/**
 * @author renyunhui
 * @description 这是一个OptionalExample类
 * @date 2024-12-26 15-14-12
 * @since 1.0.0
 */
public class OptionalExample {

    public static void main(String[] args) {
        String sex = "男";
        test(sex);
    }

    private static void test(String sex) {
        Optional<String> optionalSex = Optional.ofNullable(sex);
        optionalSex.filter("男"::equals).map(s -> {
            System.out.println("执行 A 逻辑");
            return s;
        }).orElseGet(() -> {
            System.out.println("执行 B 逻辑");
            return null;
        });
    }
}
