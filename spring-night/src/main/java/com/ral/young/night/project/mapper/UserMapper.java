package com.ral.young.night.project.mapper;

import com.ral.young.night.project.po.User;

import java.util.List;

/**
 * night_user 持久层接口
 *
 * @author renyunhui
 * @date 2024-06-26 14:47
 * @since 1.0.0
 */
public interface UserMapper extends ExpandBaseMapper<User> {

    /**
     * 批量修改用户信息
     * @param user 用户信息
     * @return 修改数量
     */
    int batchUpdateUserName(List<User> user);
}
