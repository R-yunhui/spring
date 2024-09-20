package com.ral.young.bo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ral.young.enums.ResourceEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author renyunhui
 * @description 资源告警规则实体
 * @date 2024-09-19 16-44-03
 * @since 1.2.0
 */
@Data
@TableName(value = "kl_ops_resource_alarm_rule")
public class ResourceAlarmRule {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 资源类型
     */
    @TableField(value = "resource")
    private ResourceEnum resourceEnum;

    /**
     * 配置的阈值
     */
    @TableField(value = "threshold")
    private Double threshold;

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 采集时长
     */
    @TableField(value = "time_duration")
    private Long timeDuration;

    /**
     * 删除标志 正常：0  删除：1
     */
    @TableField(value = "delete_flag")
    private Byte deleteFlag;

    /**
     * 创建人
     */
    @TableField(value = "creator_id", fill = FieldFill.INSERT)
    private Long creatorId;

    /**
     * 修改人
     */
    @TableField(value = "updater_id", fill = FieldFill.INSERT_UPDATE)
    private Long updaterId;

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
