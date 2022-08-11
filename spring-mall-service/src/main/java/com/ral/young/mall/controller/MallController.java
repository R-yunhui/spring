package com.ral.young.mall.controller;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 *
 * @author renyunhui
 * @date 2022-08-11 13:51
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1/mall")
public class MallController {

    /**
     * 注入一个具备负载均衡能力的 RestTemplate   @LoadBalanced 中的 @Qualifier 限定注入到 list 中的 RestTemplate
     */
    @Resource
    @LoadBalanced
    private RestTemplate restTemplate;

    @GetMapping(value = "/goods")
    public String goods() {
        return "I am mall service";
    }
}
