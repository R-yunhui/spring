package com.ral.admin.provider.service;

import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.common.entity.BookInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-12 17:20
 * @Describe:
 * @Modify:
 */
@Service
@Slf4j
public class WebService {

    public BaseResult<String> sayProvider(String name) {
        log.info("调用springcloud-provider服务成功  " + name);
        return BaseResult.success("hello sayProvider " + name);
    }

    public BaseResult<BookInfo> getBookById(Integer id) {
        log.info("调用调用springcloud-provider服务查询书本信息成功：" + id);
        if (id <= 0) {
            throw new RuntimeException("获取书本信息异常");
        }
        return BaseResult.success(BookInfo.builder()
                .id(id).bookName("三国演义").author("罗贯中")
                .build());
    }

}
