package com.ral.young.transaction.service;

import com.ral.young.transaction.User;

/**
 * @author renyunhui
 * @date 2022-07-06 17:21
 * @since 1.0.0
 */
public interface IUserService {

    /**
     * 添加用户
     *
     * @param user 用户信息
     * @return 添加成功的数量
     */
    int addUser(User user);

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 修改成功的数量
     */
    int updateUser(User user);

    /**
     * 查询用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    User selectUser(int id);
}
