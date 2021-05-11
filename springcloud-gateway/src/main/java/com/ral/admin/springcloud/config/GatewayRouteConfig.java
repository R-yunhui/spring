package com.ral.admin.springcloud.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.ral.admin.springcloud.handler.filter.VipUserGatewayFilterFactory;
import com.ral.admin.springcloud.handler.pridicate.VipUserRoutePredicateFactory;
import com.ral.admin.springcloud.handler.route.NacosRouteDefinitionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 11:20
 * @Describe:
 * @Modify:
 */
@Configuration
public class GatewayRouteConfig {

    @Bean
    public VipUserRoutePredicateFactory vipUserRoutePredicateFactory() {
        return new VipUserRoutePredicateFactory();
    }

    @Bean
    public VipUserGatewayFilterFactory vipUserGatewayFilterFactory() {
        return new VipUserGatewayFilterFactory();
    }

    @Configuration
    public static class NacosDyncRoute {
        @Resource
        private NacosConfigProperties nacosConfigProperties;
        @Resource
        private ApplicationEventPublisher applicationEventPublisher;

        @Bean
        public NacosRouteDefinitionRepository nacosRouteDefinitionRepository() {
            return new NacosRouteDefinitionRepository(applicationEventPublisher, nacosConfigProperties);
        }
    }

    //@Bean(name = "PathKeyResolver")
    //public PathKeyResolver pathKeyResolver() {
    //    return new PathKeyResolver();
    //}

    @Bean(name = "HostKeyResolver")
    public HostKeyResolver hostKeyResolver() {
        return new HostKeyResolver();
    }
}
