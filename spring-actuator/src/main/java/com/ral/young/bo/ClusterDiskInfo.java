package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个ClusterMemoryInfo类
 * @date 2024-08-21 15-44-20
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterDiskInfo {

    private Double allDiskSize;

    private Double usageDiskSize;

    private long timestamp;

    private String time;
}