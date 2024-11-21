package com.ral.young.spring.basic.service.impl;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.ral.young.spring.basic.dto.UserQueryDTO;
import com.ral.young.spring.basic.entity.User;
import com.ral.young.spring.basic.exception.BusinessException;
import com.ral.young.spring.basic.exception.ErrorCodeEnum;
import com.ral.young.spring.basic.mapper.UserMapper;
import com.ral.young.spring.basic.service.UserService;
import com.ral.young.spring.basic.util.IdGenerator;
import com.ral.young.spring.basic.util.RandomDataUtil;
import com.ral.young.spring.basic.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 
 * @author young
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserService userService;
    @Resource
    private IdGenerator idGenerator;

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
        user.setId(idGenerator.nextId());
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
    public UserVO updateUser(UserVO userVO) {
        log.info("更新用户信息，请求参数：{}", userVO);
        long id = userVO.getId();
        // 先查询，获取当前版本号
        User existUser = this.getById(id);
        if (existUser == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        
        // 转换并设置版本号
        User user = userVO.toEntity();
        user.setVersion(existUser.getVersion()); // 设置当前版本号
        user.setUpdateUser(1L);
        
        // 更新（MyBatis-Plus会自动处理版本号）
        boolean success = this.updateById(user);
        if (!success) {
            throw new BusinessException(ErrorCodeEnum.USER_UPDATE_CONFLICT);
        }
        
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
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateTestUsers(int count) {
        log.info("开始批量创建测试用户，每种方法数量：{}", count);
        
        StopWatch stopWatch = new StopWatch("批量插入性能测试");
        
        // 生成两批测试数据
        stopWatch.start("生成测试数据");
        List<User> users1 = generateTestUsers(count);
        List<User> users2 = generateTestUsers(count);
        stopWatch.stop();
        
        // 使用saveBatch方法插入
        executeSaveBatch(users1, stopWatch);
        
        // 使用自定义批量插入方法
        executeCustomBatchInsert(users2, stopWatch);
        
        // 打印性能统计结果
        log.info("批量插入性能测试完成：\n{}", stopWatch.prettyPrint(TimeUnit.SECONDS));
        return true;
    }
    
    /**
     * 生成测试用户数据
     */
    private List<User> generateTestUsers(int count) {
        return new ArrayList<>(RandomDataUtil.generateRandomUsers(count));
    }
    
    /**
     * 执行MyBatis-Plus的saveBatch方法
     */
    private void executeSaveBatch(List<User> users, StopWatch stopWatch) {
        stopWatch.start("MyBatis-Plus saveBatch方法");
        try {
            boolean success = userService.saveBatch(users, 1000);
            if (!success) {
                throw new BusinessException(ErrorCodeEnum.BATCH_INSERT_ERROR);
            }
        } catch (Exception e) {
            log.error("saveBatch方法批量插入失败", e);
            throw new BusinessException(ErrorCodeEnum.BATCH_INSERT_ERROR);
        } finally {
            stopWatch.stop();
        }
    }
    
    /**
     * 执行自定义的批量插入方法
     */
    private void executeCustomBatchInsert(List<User> users, StopWatch stopWatch) {
        stopWatch.start("自定义批量插入方法");
        try {
            // 分批次插入，每批5000条
            int batchSize = 5000;
            List<List<User>> batches = Lists.partition(users, batchSize);
            
            int totalInserted = 0;
            for (int i = 0; i < batches.size(); i++) {
                List<User> batch = batches.get(i);
                int inserted = baseMapper.insertBatchSomeColumn(batch);
                totalInserted += inserted;
                
                log.info("完成第{}批数据插入，本批数量：{}", (i + 1), batch.size());
            }
            
            if (totalInserted != users.size()) {
                throw new BusinessException(ErrorCodeEnum.BATCH_INSERT_ERROR);
            }
        } catch (Exception e) {
            log.error("自定义批量插入方法失败", e);
            throw new BusinessException(ErrorCodeEnum.BATCH_INSERT_ERROR);
        } finally {
            stopWatch.stop();
        }
    }

    /**
     * 测试并发更新
     */
    @Override
    public void testConcurrentUpdate(Long id) {
        // 模拟两个线程同时更新
        User user1 = this.getById(id);
        User user2 = this.getById(id);
        
        // 线程1更新
        user1.setName("线程1");
        boolean success1 = this.updateById(user1);
        log.info("线程1更新{}", success1 ? "成功" : "失败");
        
        // 线程2更新（会失败，因为版本号不匹配）
        user2.setName("线程2");
        boolean success2 = this.updateById(user2);
        log.info("线程2更新{}", success2 ? "成功" : "失败");
    }

    @Override
    public IPage<UserVO> pageUsers(UserQueryDTO queryDTO) {
        log.info("分页查询用户，参数：{}", queryDTO);
        
        // 执行分页查询
        Page<User> userPage = this.lambdaQuery()
                .like(StrUtil.isNotBlank(queryDTO.getName()), User::getName, queryDTO.getName())
                .eq(ObjectUtil.isNotNull(queryDTO.getGender()), User::getGender, queryDTO.getGender())
                .like(StrUtil.isNotBlank(queryDTO.getEmail()), User::getEmail, queryDTO.getEmail())
                .ge(ObjectUtil.isNotNull(queryDTO.getCreateTimeStart()), User::getCreateTime, queryDTO.getCreateTimeStart())
                .le(ObjectUtil.isNotNull(queryDTO.getCreateTimeEnd()), User::getCreateTime, queryDTO.getCreateTimeEnd())
                .orderByDesc(User::getCreateTime)
                .page(new Page<>(queryDTO.getCurrent(), queryDTO.getSize()));
        
        // 转换为VO对象
        return convertToVoPage(userPage);
    }
    
    /**
     * 将实体分页对象转换为VO分页对象
     */
    private Page<UserVO> convertToVoPage(Page<User> entityPage) {
        Page<UserVO> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(UserVO::fromEntity)
                .collect(Collectors.toList()));
        return voPage;
    }
} 