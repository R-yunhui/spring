package com.ral.young.spring.basic.metrics.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MetricsMonitor {
    /**
     * 指标名称
     */
    String name();
    
    /**
     * 指标描述
     */
    String description() default "";
    
    /**
     * 标签键值对，格式：key=value
     */
    String[] tags() default {};
} 