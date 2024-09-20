package com.ral.young.vo;

import com.ral.young.enums.ResourceEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个ResourceAlarmRuleVO类
 * @date 2024-09-19 17-25-11
 * @since 1.2.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceAlarmRuleVO {

    private Double threshold;

    private Long timeDuration;

    private ResourceEnum resourceEnum;
}
