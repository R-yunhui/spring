package com.ral.young.controller;

import com.ral.young.po.ResourceInfo;
import com.ral.young.service.IResourceInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * @author renyunhui
 * @date 2024-07-23 15:16
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Resource
    private IResourceInfoService resourceInfoService;

    @GetMapping(value = "/helloWorld")
    public String testHelloWorld() {
        return "hello world !!";
    }

    @PostMapping(value = "/createResource")
    public int createResource(@RequestBody ResourceInfo resourceInfo) {
        return resourceInfoService.createResource(resourceInfo);
    }
}
