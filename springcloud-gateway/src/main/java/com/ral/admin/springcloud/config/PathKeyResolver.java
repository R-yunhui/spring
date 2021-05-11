package com.ral.admin.springcloud.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 15:06
 * @Describe:
 * @Modify:
 */
public class PathKeyResolver implements KeyResolver {
    
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        // 根据uri去限流
        // 限流的规则辉作用在路径上
        // 访问 http://localhost:8090/requestRateLimiterGatewayFilterFactory
        // 限流规则：redis-rate-limiter.replenishRate: 1  # 每秒允许处理的请求数量（令牌桶每秒填充平均速率）
        //         redis-rate-limiter.burstCapacity: 2  # 每秒最大处理的请求数量（令牌桶总容量）
        return Mono.just(exchange.getRequest().getURI().getPath());
    }
}
