package com.ral.young.transaction;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring事务的配置类
 *
 * @author renyunhui
 * @date 2022-07-06 10:44
 * @since 1.0.0
 */
@Configuration
@ComponentScan(value = "com.ral.young.transaction")
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class TransactionConfig {
}
