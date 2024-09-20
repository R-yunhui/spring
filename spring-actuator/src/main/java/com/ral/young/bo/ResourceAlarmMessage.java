package com.ral.young.bo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ral.young.enums.ResourceEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author renyunhui
 * @description 资源告警信息实体
 * @date 2024-09-19 16-44-03
 * @since 1.2.0
 */
@Data
@TableName(value = "kl_ops_resource_alarm_message")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceAlarmMessage {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 告警消息
     */
    @TableField(value = "message")
    private String message;

    /**
     * 资源类型
     */
    @TableField(value = "resource")
    private ResourceEnum resourceEnum;

    /**
     * 删除标志 正常：0  删除：1
     */
    @TableField(value = "delete_flag")
    private Byte deleteFlag;

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 所属规则 id
     */
    @TableField(value = "rule_id")
    private Long ruleId;

    /**
     * 告警时间
     */
    @TableField(value = "alarm_time")
    private Date alarmTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
