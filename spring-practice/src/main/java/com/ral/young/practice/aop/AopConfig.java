package com.ral.young.practice.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *
 * @author renyunhui
 * @date 2023-05-24 10:54
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.practice.aop")
@EnableAspectJAutoProxy
public class AopConfig {


}
