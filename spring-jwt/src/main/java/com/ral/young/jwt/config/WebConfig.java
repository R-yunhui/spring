package com.ral.young.jwt.config;

import com.ral.young.jwt.filter.TokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * web 配置
 *
 * @author renyunhui
 * @date 2023-06-14 11:26
 * @since 1.0.0
 */
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<TokenFilter> registerMyFilter() {
        FilterRegistrationBean<TokenFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(1);
        bean.setFilter(new TokenFilter());
        // 针对配置的请求进行过滤
        bean.addUrlPatterns("/test/user/*");
        return bean;
    }
}
