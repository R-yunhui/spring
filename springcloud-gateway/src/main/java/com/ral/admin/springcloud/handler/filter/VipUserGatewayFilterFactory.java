package com.ral.admin.springcloud.handler.filter;

import com.ral.admin.springcloud.handler.pridicate.VipUserRoutePredicateFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 16:29
 * @Describe:
 * @Modify:
 */
public class VipUserGatewayFilterFactory extends AbstractGatewayFilterFactory<VipUserGatewayFilterFactory.Config> {

    private static final String NOT_VIP = "当前登录的用户不是VIP用户，不允许访问";

    public VipUserGatewayFilterFactory() {
        super(VipUserGatewayFilterFactory.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String username = exchange.getRequest().getQueryParams().getFirst("username");
            // TODO 判断用户是否是VIP用户
            if (null != username && username.equals(config.name)) {
                return chain.filter(exchange);
            } else {
                ServerHttpResponse response = exchange.getResponse();
                byte[] bits = NOT_VIP.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bits);
                // 指定编码，以防乱码
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                return response.writeWith(Mono.just(buffer));
            }
        };
    }

    public static class Config {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
