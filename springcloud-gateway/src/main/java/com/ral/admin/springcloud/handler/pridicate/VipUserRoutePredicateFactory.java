package com.ral.admin.springcloud.handler.pridicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.http.HttpCookie;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.function.Predicate;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 11:10
 * @Describe: VIP用户路由谓词工厂
 * @Modify:
 */
public class VipUserRoutePredicateFactory extends AbstractRoutePredicateFactory<VipUserRoutePredicateFactory.Config> {

    public VipUserRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return (GatewayPredicate) serverWebExchange -> {
            List<HttpCookie> cookies = serverWebExchange.getRequest().getCookies().get(config.getVipTag());
            boolean isVip = false;
            if (!CollectionUtils.isEmpty(cookies)) {
                // TODO 判断cookies中的参数信息是否符合VIP用户
                isVip = true;
            }
            return isVip;
        };
    }

    public static class Config {

        /**
         * VIP用户标识
         */
        private String vipTag = "vipTag";

        public String getVipTag() {
            return vipTag;
        }

        public void setVipTag(String vipTag) {
            this.vipTag = vipTag;
        }
    }
}
