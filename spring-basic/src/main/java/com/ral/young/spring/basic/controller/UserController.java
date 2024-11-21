package com.ral.young.spring.basic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ral.young.spring.basic.common.Result;
import com.ral.young.spring.basic.dto.UserQueryDTO;
import com.ral.young.spring.basic.service.CustomValidateGroup;
import com.ral.young.spring.basic.service.UserService;
import com.ral.young.spring.basic.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 用户管理控制器
 *
 * @author young
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理接口")
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
    @ApiOperation(value = "创建用户", notes = "创建新用户，返回用户完整信息")
    @PostMapping
    public Result<UserVO> createUser(
            @ApiParam(value = "用户信息", required = true)
            @RequestBody @Validated UserVO userVO) {
        return Result.success(userService.createUser(userVO));
    }

    /**
     * 更新用户信息
     *
     * @param userVO 用户信息
     * @return 更新后的用户信息
     */
    @ApiOperation(value = "更新用户", notes = "根据用户ID更新用户信息")
    @PutMapping("/{id}")
    public Result<UserVO> updateUser(
            @ApiParam(value = "用户信息", required = true)
            @RequestBody @Validated(value = {CustomValidateGroup.Crud.Update.class}) UserVO userVO) {
        return Result.success(userService.updateUser(userVO));
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @ApiOperation(value = "获取用户信息", notes = "根据用户ID获取用户详细信息")
    @GetMapping("/{id}")
    public Result<UserVO> getUser(
            @ApiParam(value = "用户ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除用户", notes = "根据用户ID删除用户信息（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @ApiParam(value = "用户ID", required = true, example = "1")
            @PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    /**
     * 批量创建测试用户
     */
    @ApiOperation(value = "批量创建测试用户", notes = "创建指定数量的测试用户数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "count", value = "创建数量", required = true, dataType = "Integer", example = "1000")
    })
    @PostMapping("/batch-test")
    public Result<Boolean> batchCreateTestUsers(
            @RequestParam @Min(1) @Max(100000) Integer count) {
        return Result.success(userService.batchCreateTestUsers(count));
    }

    /**
     * 测试乐观锁并发更新
     */
    @ApiOperation(value = "测试乐观锁", notes = "模拟并发更新场景")
    @PostMapping("/{id}/test-concurrent")
    public Result<Void> testConcurrentUpdate(
            @ApiParam(value = "用户ID", required = true, example = "1")
            @PathVariable Long id) {
        userService.testConcurrentUpdate(id);
        return Result.success();
    }

    @ApiOperation("分页查询用户")
    @PostMapping("/page")
    public Result<IPage<UserVO>> pageUsers(@RequestBody @Validated UserQueryDTO queryDTO) {
        return Result.success(userService.pageUsers(queryDTO));
    }
}