package com.ral.young.controller;

import com.ral.young.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @description 这是一个TestController类
 * @date 2024-09-11 09-54-10
 * @since 1.0.0
 */
@RestController
public class TestController {

    @Resource
    private UserService userService;

    @GetMapping(value = "/test")
    public void test() {
        userService.test();
    }
}
