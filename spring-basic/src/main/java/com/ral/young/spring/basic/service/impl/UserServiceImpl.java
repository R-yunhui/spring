package com.ral.young.spring.basic.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ral.young.spring.basic.entity.User;
import com.ral.young.spring.basic.exception.BusinessException;
import com.ral.young.spring.basic.exception.ErrorCodeEnum;
import com.ral.young.spring.basic.mapper.UserMapper;
import com.ral.young.spring.basic.service.UserService;
import com.ral.young.spring.basic.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 
 * @author young
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserVO userVO) {
        log.info("创建用户，请求参数：{}", userVO);
        
        // 检查邮箱是否已存在
        long count = this.lambdaQuery()
                .eq(User::getEmail, userVO.getEmail())
                .count();

        if (count > 0) {
            throw new BusinessException(ErrorCodeEnum.USER_EMAIL_EXISTS);
        }
        
        // VO转换为实体并保存
        User user = userVO.toEntity();
        // 设置默认值
        user.setIsDelete(0);
        // TODO: 设置当前登录用户ID
        user.setCreateUser(1L);
        user.setUpdateUser(1L);
        
        // 保存用户信息
        boolean success = this.save(user);
        if (!success) {
            throw new BusinessException(ErrorCodeEnum.USER_CREATE_ERROR);
        }
        
        log.info("用户创建成功，用户ID：{}", user.getId());
        return UserVO.fromEntity(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUser(Long id, UserVO userVO) {
        log.info("更新用户信息，用户ID：{}，请求参数：{}", id, userVO);
        
        // 检查用户是否存在
        User existUser = this.getById(id);
        if (existUser == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        
        // 检查邮箱是否被其他用户使用
        long count = this.lambdaQuery()
                .eq(User::getEmail, userVO.getEmail())
                .ne(User::getId, id)
                .count();
        if (count > 0) {
            throw new BusinessException(ErrorCodeEnum.USER_EMAIL_EXISTS);
        }
        
        // VO转换为实体并更新
        User user = userVO.toEntity();
        user.setId(id);
        // TODO: 设置当前登录用户ID
        user.setUpdateUser(1L);
        
        // 更新用户信息
        boolean success = this.updateById(user);
        if (!success) {
            throw new BusinessException(ErrorCodeEnum.USER_UPDATE_ERROR);
        }
        
        log.info("用户信息更新成功，用户ID：{}", id);
        return UserVO.fromEntity(this.getById(id));
    }

    @Override
    public UserVO getUserById(Long id) {
        log.info("获取用户信息，用户ID：{}", id);
        
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        
        return UserVO.fromEntity(user);
    }

    @Override
    public Page<UserVO> listUsersByPage(Integer current, Integer size) {
        log.info("分页查询用户列表，当前页：{}，每页大小：{}", current, size);
        
        // 查询用户列表
        Page<User> page = this.page(new Page<>(current, size));
        
        // 转换为VO
        Page<UserVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream()
                .map(UserVO::fromEntity)
                .collect(Collectors.toList()));
        
        return voPage;
    }
} 