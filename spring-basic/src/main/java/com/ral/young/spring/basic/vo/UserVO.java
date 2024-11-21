package com.ral.young.spring.basic.vo;

import com.ral.young.spring.basic.entity.User;
import com.ral.young.spring.basic.enums.CommonEnum.GenderEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户信息VO")
public class UserVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    private String name;

    @Schema(description = "年龄", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;

    @Schema(description = "邮箱", example = "zhangsan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "性别(0-未知，1-男，2-女)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "性别不能为空")
    private Integer gender;      // 性别值

    @Schema(description = "性别描述", example = "男")
    private String genderDesc;   // 性别描述

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人ID", example = "1")
    private Long createUser;

    @Schema(description = "更新人ID", example = "1")
    private Long updateUser;

    // VO转Entity
    public User toEntity() {
        User user = new User();
        BeanUtils.copyProperties(this, user, "gender", "genderDesc");
        user.setGender(GenderEnum.fromValue(this.gender));
        return user;
    }

    // Entity转VO
    public static UserVO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo, "gender");
        if (user.getGender() != null) {
            vo.setGender(user.getGender().getValue());
            vo.setGenderDesc(user.getGender().getDesc());
        }
        return vo;
    }
} 