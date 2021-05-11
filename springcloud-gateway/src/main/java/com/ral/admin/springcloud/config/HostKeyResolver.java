package com.ral.admin.springcloud.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 16:16
 * @Describe: 基于用户进行限流
 * @Modify:
 */
public class HostKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(Objects.requireNonNull(exchange.getRequest().getQueryParams().getFirst("username")));
    }
}
