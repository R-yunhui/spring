package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author renyunhui
 * @description 统计 gpu 利用率
 * @date 2024-08-26 09-30-02
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GpuInfo {

    private String nodeName;

    private String instance;

    private String gpuModelName;

    private List<GpuCardInfo> gpuCardInfos;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class GpuCardInfo {
        private String gpuIndex;

        private Double gpuMemorySize;

        private Double gpuMemoryUsed;

        private Double gpuUtilizationRate;

        private Boolean canSelect;
    }
}
