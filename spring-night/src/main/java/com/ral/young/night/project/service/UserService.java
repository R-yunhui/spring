package com.ral.young.night.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.night.project.po.User;

/**
 * 业务接口
 *
 * @author renyunhui
 * @date 2024-06-26 14:48
 * @since 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 初始化用户数据
     * @param size 初始化用户数量
     * @return 成功初始化用户的数量
     */
    int initUserInfo(int size);

    /**
     * 批量修改用户信息
     * @return 修改成功的行数
     */
    int updateBatchUser();
}
