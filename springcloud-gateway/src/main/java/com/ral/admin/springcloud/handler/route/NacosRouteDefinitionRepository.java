package com.ral.admin.springcloud.handler.route;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-10 19:28
 * @Describe:
 * @Modify:
 */
@Slf4j
public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {

    private static final String GATE_WAY_DATA_IDS = "gateway-route.json";
    private static final String GATE_WAY_GROUP_IDS = "DEFAULT_GROUP";

    private final ApplicationEventPublisher publisher;
    private final NacosConfigProperties nacosConfigProperties;

    public NacosRouteDefinitionRepository(ApplicationEventPublisher applicationEventPublisher, NacosConfigProperties nacosConfigProperties) {
        this.nacosConfigProperties = nacosConfigProperties;
        this.publisher = applicationEventPublisher;
        addListener();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            String content = nacosConfigProperties.configServiceInstance().getConfig(GATE_WAY_DATA_IDS, GATE_WAY_GROUP_IDS,5000);
            List<RouteDefinition> routeDefinitions = getListByStr(content);
            return Flux.fromIterable(routeDefinitions);
        } catch (NacosException e) {
            log.error("从nacos获取路由定义的配置信息失败", e);
        }
        return Flux.fromIterable(CollUtil.newArrayList());
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }

    /**
     * 添加Nacos监听
     */
    private void addListener() {
        try {
            nacosConfigProperties.configServiceInstance().addListener(GATE_WAY_DATA_IDS, GATE_WAY_GROUP_IDS, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                }
            });
        } catch (NacosException e) {
            log.error("监听nacos配置文件修改事件失败", e);
        }
    }

    /**
     * 获取路由定义信息
     * @param content nacos配置文件信息
     * @return 路由定义信息集合
     */
    private List<RouteDefinition> getListByStr(String content) {
        if (StringUtils.isNotEmpty(content)) {
            return JSONUtil.toList(content, RouteDefinition.class);
        }
        return new ArrayList<>(0);
    }
}
