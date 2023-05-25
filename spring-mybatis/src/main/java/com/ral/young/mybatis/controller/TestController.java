package com.ral.young.mybatis.controller;

import com.ral.young.mybatis.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @date 2023-05-24 13:53
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping(value = "/add")
    public String testAdd() {
        testService.add();
        return "add ok";
    }

    @GetMapping(value = "/update")
    public String testUpdate() {
        testService.update();
        return "update ok";
    }

    @GetMapping(value = "/query")
    public String testQuery() {
        testService.query();
        return "query ok";
    }
}
