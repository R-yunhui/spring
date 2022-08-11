package com.ral.young.boot.controller;

import com.ral.young.boot.service.IGoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @date 2022-08-03 9:43
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/goods")
public class GoodsController {

    @Resource
    private IGoodsService goodsService;

    @GetMapping(value = "/test")
    public String test() {
        return "hello world";
    }

    @GetMapping(value = "/queryAllGoods")
    public void queryAllGoods() {
        goodsService.queryAllGoods();
    }
}
