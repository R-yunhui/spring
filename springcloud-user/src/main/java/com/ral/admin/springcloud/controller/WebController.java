package com.ral.admin.springcloud.controller;

import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.common.entity.BookInfo;
import com.ral.admin.springcloud.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-13 9:37
 * @Describe:
 * @Modify:
 */
@RestController
public class WebController {

    @Resource
    private TestService testService;

    @GetMapping(value = "/getBookById")
    public Mono<BaseResult<BookInfo>> getBookById(@RequestParam(value = "id") int id) {
        return testService.getBookById(id);
    }
}
