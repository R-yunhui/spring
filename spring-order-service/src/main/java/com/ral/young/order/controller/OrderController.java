package com.ral.young.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 *
 * @author renyunhui
 * @date 2022-08-11 13:56
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/v1/order")
public class OrderController {

    @Resource
    private RestTemplate restTemplate;

    @GetMapping(value = "/order")
    public String goods() {
        String result = restTemplate.getForObject("http://mall-service/v1/mall/goods", String.class);
        System.out.println("调用服务:" + "mall-service" + " 获取到的结果为:" + result);
        return "I am order service";
    }
}
