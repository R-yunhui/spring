package com.ral.young.mybatis.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.IdUtil;
import com.ral.young.mybatis.dao.UserMapper;
import com.ral.young.mybatis.dto.UserDTO;
import com.ral.young.mybatis.enums.SexEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author renyunhui
 * @date 2023-06-08 16:16
 * @since 1.0.0
 */
@Service
@Slf4j
public class TestService {

    @Resource
    private IUserService userService;

    @Resource
    private UserMapper userMapper;

    @Transactional
    public void mockL1Cache(Long id) {
        // 模拟使用一级缓存的情况，查看控制台是否重复打印sql
        log.info("模拟一级缓存是否生效");
        log.info("第一次查询id:{}", id);
        userMapper.selectById(id);

        /*
         * 一级缓存是SqlSession级别的缓存。在操作数据库时需要构造sqlSession对象，在对象中有一个数据结构（HashMap）用于存储缓存数据。
         * 不同的sqlSession之间的缓存数据区域（HashMap）是互相不影响的。 一级缓存是默认开启的不用配置。
         *
         * 【注】：保证在事务中进行处理，否则会进行判断：org.mybatis.spring.SqlSessionUtils.isSqlSessionTransactional - 事务管理是否持有的 SqlSessionHolder，并且持有的和当前 sqlSession 是否一致，
         *  false 直接就直提交了，然后会清空缓存 clearLocalCache()
         */

        log.info("第二次查询id:{}", id);
        userMapper.selectById(id);
    }

    @Transactional
    public void testBatchSave() {
        // 测试mybatis-plus自带的批量插入功能的效率
        List<UserDTO> userDTOS = mockUserInfoList();
        StopWatch stopWatch = new StopWatch("测试两种方式批量插入的效率");
        stopWatch.start("testNormal");
        userService.saveBatch(userDTOS);
        stopWatch.stop();

        userDTOS = mockUserInfoList();
        stopWatch.start("testExtend");
        userMapper.insertBatchSomeColumn(userDTOS);
        stopWatch.stop();

        log.info("测试两种方式批量插入的效率:{}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    private static List<UserDTO> mockUserInfoList() {
        List<UserDTO> userList = new ArrayList<>(30010);
        IntStream.range(0, 30000).forEach(o -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(IdUtil.getSnowflakeNextId());
            userDTO.setName(IdUtil.fastSimpleUUID());
            userDTO.setAge(ThreadLocalRandom.current().nextInt(10) + 20);
            userDTO.setSex(SexEnum.MAN);
            userDTO.setUpdateUser(IdUtil.getSnowflakeNextId());
            userDTO.setCreateUser(IdUtil.getSnowflakeNextId());
            userDTO.setCreateTime(DateUtil.date());
            userDTO.setUpdateTime(DateUtil.date());
            userList.add(userDTO);
        });
        return userList;
    }
}
