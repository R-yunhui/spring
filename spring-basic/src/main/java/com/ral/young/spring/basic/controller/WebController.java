package com.ral.young.spring.basic.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author renyunhui
 * @description 这是一个WebController类
 * @date 2024-11-21 10-06-23
 * @since 1.0.0
 */
@RestController
@Api(tags = "Web管理API")
public class WebController {

    @GetMapping("/hello")
    @ApiOperation(value = "返回Hello World字符串", notes = "这是一个测试接口")
    public String helloWorld() {
        return "Hello World";
    }
}
