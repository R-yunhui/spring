package com.ral.young.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ral.young.po.ResourceInfo;
import com.ral.young.po.ResourceMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源消息 mapper
 *
 * @author renyunhui
 * @date 2024-07-23 15:38
 * @since 1.0.0
 */
@Mapper
public interface ResourceMessageMapper extends BaseMapper<ResourceMessage> {
}
