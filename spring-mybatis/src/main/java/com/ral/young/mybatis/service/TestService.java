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
