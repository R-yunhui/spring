package com.ral.young.spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author renyunhui
 * @description 控制器层 demo
 * @date 2025-01-20 11-41-38
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/web")
public class WebController {

    @GetMapping(value = "/sayHello")
    public String sayHello() {
        return "hello world";
    }
}
