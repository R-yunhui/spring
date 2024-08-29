package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author renyunhui
 * @description 查询到的节点资源变化情况
 * @date 2024-08-28 14-54-51
 * @since 1.1.2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeResourceVariationInfo {

    private String nodeName;

    private String instance;

    private Map<Long, String> variationInfoMap;

    private Map<Long, Double> receiveBytes;

    private Map<Long, Double> sendBytes;
}
