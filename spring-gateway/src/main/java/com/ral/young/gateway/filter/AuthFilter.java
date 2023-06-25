package com.ral.young.gateway.filter;

import cn.hutool.core.text.CharSequenceUtil;
import com.ral.young.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限全局过滤器
 * {@link GlobalFilter}
 *
 * @author renyunhui
 * @date 2023-06-20 14:09
 * @since 1.0.0
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter {

    /**
     * 不需要进行权限校验的接口地址集合
     * 后续可以放在 nacos 的配置文件中，进行动态的刷新
     */
    private static final List<String> NOT_AUTH_URL_LIST = new ArrayList<>();

    static {
        NOT_AUTH_URL_LIST.add("/register");
        NOT_AUTH_URL_LIST.add("/test");
        NOT_AUTH_URL_LIST.add("/getToken");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.先判断用户访问的接口是否可以不进行权限的校验
        String url = exchange.getRequest().getURI().getPath();
        if (NOT_AUTH_URL_LIST.stream().anyMatch(url::contains)) {
            return chain.filter(exchange);
        }

        // 进行接口的鉴权，直接在 API 网关中鉴定即可，后续单独拉取一个鉴权的中心服务
        String token = exchange.getRequest().getHeaders().getFirst("token");

        if (CharSequenceUtil.isBlank(token)) {
            token = exchange.getRequest().getHeaders().getFirst("authorization");
        }

        boolean success = JwtUtil.checkToken(token);
        // 鉴权失败
        if (!success) {
            log.error("token 鉴权失败,请重试");
            throw new RuntimeException("token 鉴权失败,请重试");
        }

        return chain.filter(exchange);
    }
}
