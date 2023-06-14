package com.ral.young.jwt.controller;

import com.ral.young.jwt.common.BaseResult;
import com.ral.young.jwt.service.TestService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @date 2023-06-14 11:23
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Resource
    private TestService testService;

    @RequestMapping(value = "/getToken/{id}/{name}")
    public BaseResult<String> getToken(@PathVariable String id, @PathVariable String name) {
        return BaseResult.success(testService.getToken(id, name));
    }

    @RequestMapping(value = "/user/testToken")
    public BaseResult<String> testToken() {
        return BaseResult.success(testService.testToken());
    }
}
