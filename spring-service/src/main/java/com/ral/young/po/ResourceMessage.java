package com.ral.young.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 资源消息的本地消息表实体数据
 *
 * @author renyunhui
 * @date 2024-07-23 15:34
 * @since 1.0.0
 */
@TableName(value = "ral_resource_message")
@Data
public class ResourceMessage {

    @TableId
    private Long id;

    @TableField
    private String messageInfo;

    @TableField
    private Byte status;
}
