package com.ral.admin.springcloud.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ral.admin.springcloud.common.BaseResult;
import com.ral.admin.springcloud.common.entity.BookInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-13 11:02
 * @Describe: 默认的sentinel 限流降级爆出的 BlockException 逻辑处理类
 * @Modify:
 */
@Component
@Slf4j
public class DefaultBlockHandler {

    public BaseResult<BookInfo> getBookByIdBlockHandler(int id, BlockException e) {
        log.error("调用ProviderService服务通过书本ID：{} 来获取书本信息失败：{}", id , e);
        return BaseResult.failure(600, e.getMessage(), BookInfo.builder()
                .id(0)
                .bookName("默认书本")
                .author("默认作者")
                .build());
    }
}
