package com.ral.young.night.interview.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 *
 * @author renyunhui
 * @date 2024-06-25 9:38
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.night.interview")
@EnableTransactionManagement
public class InterviewConfig {
}
