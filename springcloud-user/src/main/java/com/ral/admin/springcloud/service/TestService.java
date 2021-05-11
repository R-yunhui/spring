package com.ral.admin.springcloud.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.common.entity.BookInfo;
import com.ral.admin.springcloud.feign.ProviderFeignService;
import com.ral.admin.springcloud.sentinel.handler.DefaultBlockHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-13 11:22
 * @Describe:
 * @Modify:
 */
@Service
public class TestService {

    @Resource
    private ProviderFeignService providerFeignService;

    @SentinelResource(value = "getBookById", fallbackClass = DefaultBlockHandler.class, fallback = "getBookByIdBlockHandler")
    public Mono<BaseResult<BookInfo>> getBookById(int id) {
        return Mono.just(providerFeignService.getBookById(id));
    }
}
