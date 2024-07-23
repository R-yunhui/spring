package com.ral.young.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ral.young.po.ResourceInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源 mapper
 *
 * @author renyunhui
 * @date 2024-07-23 15:38
 * @since 1.0.0
 */
@Mapper
public interface ResourceMapper extends BaseMapper<ResourceInfo> {
}
