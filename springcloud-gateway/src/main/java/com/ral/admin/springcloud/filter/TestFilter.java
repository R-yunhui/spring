package com.ral.admin.springcloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-09 14:57
 * @Describe:
 * @Modify:
 */
@Component
@Slf4j
public class TestFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("执行TestFilter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
