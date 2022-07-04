package com.ral.young.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Aop配置文件
 *
 * @author renyunhui
 * @date 2022-06-30 14:23
 * @since 1.0.0
 */
@EnableAspectJAutoProxy
@Configuration
@ComponentScan(value = "com.ral.young.aop")
public class AopConfig {
}
