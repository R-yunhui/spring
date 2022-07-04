package com.ral.young.aop.annotation;

import java.lang.annotation.*;

/**
 * 日志注解 aop
 *
 * @author renyunhui
 * @date 2022-07-01 16:13
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyLog {

    String name() default "test";
}
