package com.ral.young.docker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author renyunhui
 * @description 这是一个TestController类
 * @date 2024-10-28 11-29-31
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping(value = "/hello")
    public String test(){
        return "hello docker";
    }
}
