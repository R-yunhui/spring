package com.ral.young.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 资源数据实体
 *
 * @author renyunhui
 * @date 2024-07-23 15:34
 * @since 1.0.0
 */
@TableName(value = "ral_resource_info")
@Data
public class ResourceInfo {

    @TableId
    private Long id;

    @TableField
    private String resourceName;
}
