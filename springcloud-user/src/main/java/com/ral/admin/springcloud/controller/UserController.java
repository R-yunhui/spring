package com.ral.admin.springcloud.controller;

import cn.hutool.core.util.IdUtil;
import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.pojo.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-08 13:39
 * @Describe:
 * @Modify:
 */
@RestController
@Slf4j
public class UserController {

    @GetMapping(value = "/getUserById/{id}")
    public BaseResult<UserDo> getUserById(@PathVariable(value = "id") int id) {
        return BaseResult.success(UserDo.builder()
                .id(id)
                .userId(IdUtil.fastSimpleUUID())
                .username("mike")
                .realName("麦克")
                .build());
    }

    @GetMapping(value = "/afterRoutePredicateFactory/{id}")
    public BaseResult<String> afterRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试AfterRoutePredicateFactory 谓词工厂：" + id);
    }

    @GetMapping(value = "/beforeRoutePredicateFactory/{id}")
    public BaseResult<String> beforeRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试AfterRoutePredicateFactory 谓词工厂：" + id);
    }

    @GetMapping(value = "/betweenRoutePredicateFactory/{id}")
    public BaseResult<String> betweenRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试BetweenRoutePredicateFactory 谓词工厂：" + id);
    }

    @GetMapping(value = "/cookieRoutePredicateFactory/{id}")
    public BaseResult<String> cookieRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试CookieRoutePredicateFactory 谓词工厂：" + id);
    }

    @GetMapping(value = "/headerRoutePredicateFactory/{id}")
    public BaseResult<String> headerRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试HeaderRoutePredicateFactory 谓词工厂：" + id);
    }

    @DeleteMapping(value = "/pathRoutePredicateFactory/{id}")
    public BaseResult<String> pathRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试PathRoutePredicateFactory 谓词工厂：" + id);
    }

    @PostMapping(value = "/methodRoutePredicateFactory/{id}")
    public BaseResult<String> methodRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试MethodRoutePredicateFactory 谓词工厂：" + id);
    }

    @GetMapping(value = "/remoteAddrRoutePredicateFactory/{id}")
    public BaseResult<String> remoteAddrRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试RemoteAddrRoutePredicateFactory 谓词工厂：" + id);
    }

    @GetMapping(value = "/hostRoutePredicateFactory/{id}")
    public BaseResult<String> hostRoutePredicateFactory(@PathVariable(value = "id") int id) {
        return BaseResult.success("访问springcloud-user服务成功,测试HostRoutePredicateFactory 谓词工厂：" + id);
    }

    @GetMapping(value = "/addRequestParameterGatewayFilterFactory")
    public BaseResult<String> addRequestParameterGatewayFilterFactory(@RequestParam(value = "username") String username) {
        log.info("username：" + username);
        return BaseResult.success("访问springcloud-user服务成功,测试AddRequestParameterGatewayFilterFactory 过滤器工厂");
    }

    @GetMapping(value = "/stripPrefixGatewayFilterFactory")
    public BaseResult<String> stripPrefixGatewayFilterFactory() {
        return BaseResult.success("访问springcloud-user服务成功,测试StripPrefixGatewayFilterFactory 过滤器工厂");
    }

    @GetMapping(value = "/test/prefixPathGatewayFilterFactory")
    public BaseResult<String> prefixPathGatewayFilterFactory() {
        return BaseResult.success("访问springcloud-user服务成功,测试PrefixPathGatewayFilterFactory 过滤器工厂");
    }

    @GetMapping(value = "/addRequestHeaderGatewayFilterFactory")
    public BaseResult<String> addRequestHeaderGatewayFilterFactory(HttpServletRequest request) {
        log.info("请求头数据:" + request.getHeader("username"));
        return BaseResult.success("访问springcloud-user服务成功,测试AddRequestHeaderGatewayFilterFactory 过滤器工厂");
    }

    @GetMapping(value = "/requestRateLimiterGatewayFilterFactory")
    public BaseResult<String> requestRateLimiterGatewayFilterFactory(HttpServletRequest request) {
        log.info("请求头数据:" + request.getHeader("username"));
        return BaseResult.success("访问springcloud-user服务成功,测试RequestRateLimiterGatewayFilterFactory 过滤器工厂");
    }

    @GetMapping(value = "/vipUserGatewayFilterFactory")
    public BaseResult<String> vipUserGatewayFilterFactory(@RequestParam(value = "username") String username) {
        log.info("请求参数:" + username);
        return BaseResult.success("访问springcloud-user服务成功,测试VipUserGatewayFilterFactory 过滤器工厂");
    }
}
