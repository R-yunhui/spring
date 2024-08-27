package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author renyunhui
 * @description gpu内存信息
 * @date 2024-08-26 14-26-23
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GpuMemoryDetail {

    private String nodeName;

    private Map<Long, String> memoryUsageMap;
}
