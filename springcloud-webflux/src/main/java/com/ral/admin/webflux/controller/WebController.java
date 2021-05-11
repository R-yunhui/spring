package com.ral.admin.webflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-11 16:29
 * @Describe:
 * @Modify:
 */
@RestController
public class WebController {

    @GetMapping(value = "/sayHello")
    public Mono<String> sayHello() {
        return Mono.just("hello world");
    }
}
