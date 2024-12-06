package com.ral.young.metrics.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 这是一个DeviceSaveData类
 * @date 2024-12-06 17-28-29
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DeviceSaveData {

    private String deviceCode;
    private String orgCode;
    private String externalOrgCode;
    private String platformIdentifier;
}
