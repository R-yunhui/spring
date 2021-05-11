package com.ral.admin.provider.controller;

import com.ral.admin.provider.service.WebService;
import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.common.entity.BookInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-12 17:20
 * @Describe:
 * @Modify:
 */
@RestController
public class WebController {

    @Value("${spring.application.name}")
    private String name;

    @Resource
    private WebService webService;

    @GetMapping(value = "/sayProvider")
    public Mono<BaseResult<String>> sayProvider() {
        return Mono.just(webService.sayProvider(name));
    }

    @GetMapping(value = "/getBookById")
    public Mono<BaseResult<BookInfo>> getBookById(@RequestParam(value = "id") Integer id) {
        return Mono.just(webService.getBookById(id));
    }
}
