package com.ral.young.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.ral.young.enums.CommonEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author renyunhui
 * @description 这是一个UserDTO类
 * @date 2024-09-11 10-07-14
 * @since 1.0.0
 */
@TableName(value = "ral_user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "ral_user_name")
    private String userName;

    @TableField(value = "ral_gender")
    private CommonEnum.GnderEnum gender;

    @TableField(value = "ral_age")
    private Integer age;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
