package com.ral.young.spring.basic.vo;

import com.ral.young.spring.basic.entity.User;
import com.ral.young.spring.basic.enums.CommonEnum.GenderEnum;
import com.ral.young.spring.basic.service.CustomValidateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户信息VO
 *
 * @author young
 */
@Data
@ApiModel(description = "用户信息")
public class UserVO {

    @ApiModelProperty(value = "用户ID", example = "1")
    @NotNull(message = "用户id不能为空", groups = {CustomValidateGroup.Crud.Update.class})
    private Long id;

    @ApiModelProperty(value = "用户名", example = "张三", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    private String name;

    @ApiModelProperty(value = "年龄", example = "25", required = true)
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;

    @ApiModelProperty(value = "邮箱", example = "zhangsan@example.com", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "性别(0-未知，1-男，2-女)", example = "1", required = true)
    @NotNull(message = "性别不能为空")
    private Integer gender;

    /**
     * VO转Entity
     *
     * @return User实体对象
     */
    public User toEntity() {
        User user = new User();
        BeanUtils.copyProperties(this, user, "gender");
        user.setGender(GenderEnum.fromValue(this.gender));
        return user;
    }

    /**
     * Entity转VO
     *
     * @param user User实体对象
     * @return UserVO对象
     */
    public static UserVO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo, "gender");
        if (user.getGender() != null) {
            vo.setGender(user.getGender().getValue());
        }
        return vo;
    }
} 