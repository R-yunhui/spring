package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class GpuMemoryInfo {

    private String nodeName;

    private String instance;

    private Double usedMemory;

    private Double freeMemory;

    private Double totalMemory;
}
