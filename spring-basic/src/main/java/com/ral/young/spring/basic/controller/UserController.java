package com.ral.young.spring.basic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ral.young.spring.basic.common.Result;
import com.ral.young.spring.basic.service.UserService;
import com.ral.young.spring.basic.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户管理控制器
 *
 * @author young
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关接口")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 创建用户
     *
     * @param userVO 用户信息
     * @return 创建后的用户信息
     */
    @Operation(summary = "创建用户", description = "创建新用户")
    @PostMapping
    public Result<UserVO> createUser(@RequestBody @Validated @Parameter(description = "用户信息", required = true) UserVO userVO) {
        return Result.success(userService.createUser(userVO));
    }

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param userVO 用户信息
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    @PutMapping("/{id}")
    public Result<UserVO> updateUser(
            @Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id, @RequestBody @Validated
    @Parameter(description = "用户信息", required = true) UserVO userVO) {
        return Result.success(userService.updateUser(id, userVO));
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    @GetMapping("/{id}")
    public Result<UserVO> getUser(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 分页查询用户列表
     *
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页用户信息
     */
    @Operation(summary = "分页查询用户", description = "分页获取用户列表")
    @GetMapping
    public Result<Page<UserVO>> listUsers(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(userService.listUsersByPage(current, size));
    }
}