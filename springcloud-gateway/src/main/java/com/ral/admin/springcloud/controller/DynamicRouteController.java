package com.ral.admin.springcloud.controller;

import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.service.DynamicRouteService;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-09 15:41
 * @Describe: 对外提供的动态路由的接口
 * @Modify:
 */
@RestController
public class DynamicRouteController {

    @Resource
    private DynamicRouteService dynamicRouteService;

    @PostMapping(value = "/addRoute")
    public BaseResult<String> addRoute(@RequestBody RouteDefinition routeDefinition) {
        return BaseResult.success(dynamicRouteService.addRoute(routeDefinition));
    }
}
