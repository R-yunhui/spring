package com.ral.admin.springcloud.feign.config;

import com.ral.admin.springcloud.feign.fallback.ProviderServiceFallBack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-13 10:42
 * @Describe:
 * @Modify:
 */
@Configuration
public class FeignConfig {

    @Bean
    public ProviderServiceFallBack providerServiceFallBack() {
        return new ProviderServiceFallBack();
    }
}
