package com.ral.young.aop.annotation;

import java.lang.annotation.*;

/**
 *
 * @author renyunhui
 * @date 2022-07-01 16:21
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyCat {

    String name() default "myCat";
}
