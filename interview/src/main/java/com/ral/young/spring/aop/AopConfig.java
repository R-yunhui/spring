package com.ral.young.spring.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *
 * @author renyunhui
 * @date 2023-02-17 10:46
 * @since 1.0.0
 */
@EnableAspectJAutoProxy
@Configuration
@ComponentScan(value = "com.ral.young.spring.aop")
public class AopConfig {
}
