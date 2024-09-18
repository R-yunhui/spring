package com.ral.young.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author renyunhui
 * @description 这是一个OrderDTO类
 * @date 2024-09-11 10-02-05
 * @since 1.0.0
 */
@TableName(value = "ral_order")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDTO {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "ral_order_name")
    private String orderName;

    @TableField(value = "ral_order_price")
    private Double orderPrice;

    @TableField(value = "ral_order_remark")
    private String orderRemark;

    @TableField(value = "ral_belong_user_id")
    private Long belongUserId;

    @TableField(value = "ral_create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "ral_update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
