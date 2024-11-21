package com.ral.young.spring.basic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ral.young.spring.basic.dto.UserQueryDTO;
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
     * @param userVO 用户信息VO
     * @return 更新后的用户信息
     */
    UserVO updateUser(UserVO userVO);

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long id);

    /**
     * 批量创建测试用户
     *
     * @param count 创建数量
     * @return 创建结果
     */
    boolean batchCreateTestUsers(int count);

    /**
     * 测试并发更新
     * @param id 用户id
     */
    void testConcurrentUpdate(Long id);

    /**
     * 分页查询用户
     */
    IPage<UserVO> pageUsers(UserQueryDTO queryDTO);
} 