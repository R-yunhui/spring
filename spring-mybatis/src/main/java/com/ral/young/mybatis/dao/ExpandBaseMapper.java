package com.ral.young.mybatis.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * 真正实现批量插入的拓展接口
 *
 * @author renyunhui
 * @date 2023-06-08 16:14
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
