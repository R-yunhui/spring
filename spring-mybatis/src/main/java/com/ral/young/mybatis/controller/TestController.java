package com.ral.young.mybatis.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.ral.young.mybatis.common.BaseResult;
import com.ral.young.mybatis.dto.UserDTO;
import com.ral.young.mybatis.service.IUserService;
import com.ral.young.mybatis.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author renyunhui
 * @date 2023-06-08 14:26
 * @since 1.0.0
 */
@RestController()
@RequestMapping(value = "/test/user")
public class TestController {

    @Resource
    private IUserService userService;

    @Resource
    private TestService testService;

    @GetMapping("/{userId}")
    public BaseResult<UserDTO> getUser(@PathVariable(value = "userId") Long userId) {
        UserDTO userDTO = userService.getById(userId);
        return BaseResult.success(userDTO);
    }

    @GetMapping("/mockL1Cache/{userId}")
    public BaseResult<Void> mockL1Cache(@PathVariable(value = "userId") Long userId) {
        testService.mockL1Cache(userId);
        return BaseResult.success();
    }

    @PostMapping
    public BaseResult<String> registerUser(@RequestBody UserDTO userDTO) {
        userDTO.setCreateTime(DateUtil.date());
        userDTO.setUpdateTime(DateUtil.date());
        userDTO.setCreateUser(IdUtil.getSnowflakeNextId());
        userDTO.setUpdateUser(IdUtil.getSnowflakeNextId());

        userService.save(userDTO);
        return BaseResult.success();
    }

    @GetMapping(value = "/testBatchSave")
    public BaseResult<String> testOne() {
        testService.testBatchSave();
        return BaseResult.success();
    }
}
