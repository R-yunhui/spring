package com.ral.young.metrics.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个OrgData类
 * @date 2024-12-06 17-14-35
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrgData {

    private Long id;

    private Long parentId;

    private String orgName;
}
