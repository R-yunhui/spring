package com.ral.young.spring.basic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.spring.basic.entity.User;
import com.ral.young.spring.basic.vo.UserVO;

/**
 * 用户服务接口
 * 
 * @author young
 */
public interface UserService extends IService<User> {
    
    /**
     * 创建用户
     *
     * @param userVO 用户信息VO
     * @return 创建后的用户信息
     */
    UserVO createUser(UserVO userVO);
    
    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param userVO 用户信息VO
     * @return 更新后的用户信息
     */
    UserVO updateUser(Long id, UserVO userVO);
    
    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long id);
    
    /**
     * 分页查询用户列表
     *
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页用户信息
     */
    Page<UserVO> listUsersByPage(Integer current, Integer size);
    
    /**
     * 批量创建测试用户
     *
     * @param count 创建数量
     * @return 创建结果
     */
    boolean batchCreateTestUsers(int count);
} 