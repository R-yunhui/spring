package com.ral.admin.springcloud.feign;

import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.common.entity.BookInfo;
import com.ral.admin.springcloud.feign.config.FeignConfig;
import com.ral.admin.springcloud.feign.fallback.ProviderServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-12 17:54
 * @Describe:
 * @Modify:
 */
@FeignClient(name = "springcloud-provider-service-9090", configuration = FeignConfig.class, fallback = ProviderServiceFallBack.class)
public interface ProviderFeignService {

    /**
     * 调用provider服务的接口 sayProvider
     *
     * @return
     */
    @GetMapping(value = "/sayProvider")
    BaseResult<String> sayProvider();

    /**
     * 通过ID查询书本信息
     *
     * @param id 书本的ID
     * @return 匹配的书本信息
     */
    @GetMapping(value = "/getBookById")
    BaseResult<BookInfo> getBookById(@RequestParam(value = "id") Integer id);
}
