package com.ral.admin.springcloud.feign.fallback;

import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.common.entity.BookInfo;
import com.ral.admin.springcloud.feign.ProviderFeignService;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-13 10:41
 * @Describe: 服务远程调用回调类
 * @Modify:
 */
public class ProviderServiceFallBack implements ProviderFeignService {

    @Override
    public BaseResult<String> sayProvider() {
        return BaseResult.failure(600, "远程调用sayProvider()失败,请稍后再试");
    }

    @Override
    public BaseResult<BookInfo> getBookById(@RequestParam(value = "id") Integer id) {
        throw new RuntimeException("远程调用getBookById()失败,请稍后再试");
        // return BaseResult.failure(600, "远程调用getBookById()失败,请稍后再试");
    }
}
