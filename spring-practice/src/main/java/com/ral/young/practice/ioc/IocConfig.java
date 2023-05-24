package com.ral.young.practice.ioc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * TODO
 *
 * @author renyunhui
 * @date 2023-05-23 15:00
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.practice.ioc")
@EnableAsync
public class IocConfig {


}
