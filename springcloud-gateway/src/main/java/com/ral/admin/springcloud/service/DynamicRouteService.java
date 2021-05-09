package com.ral.admin.springcloud.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-09 15:33
 * @Describe: 动态路由 通过gateway提供的接口
 * @Modify:
 */
@Service
@Slf4j
public class DynamicRouteService implements ApplicationEventPublisherAware {

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    /**
     * 通知刷新路由的缓存
     */
    public void notifyRouteChanged() {
        this.publisher.publishEvent(new RefreshRoutesEvent((this)));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 添加路由信息
     * @param routeDefinition 路由定义器
     * @return 成功
     */
    public String addRoute(RouteDefinition routeDefinition) {
        routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        notifyRouteChanged();
        log.info("添加路由信息成功:" + JSON.toJSONString(routeDefinition));
        return "add success";
    }

    public String deleteRoute(String id) {
        routeDefinitionWriter.delete(Mono.just(id));
        notifyRouteChanged();
        log.info("删除路由信息成功:" + id);
        return "delete success";
    }
}
