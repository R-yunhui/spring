package com.ral.young.spring.basic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * @author renyunhui
 * @description 这是一个ExpandBaseMapper类
 * @date 2024-11-21 14-14-26
 * @since 1.0.0
 */
public interface ExpandBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入 仅适用于mysql
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(Collection<T> entityList);
}
