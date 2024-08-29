package com.ral.young.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author renyunhui
 * @description 查询到的节点资源信息
 * @date 2024-08-28 14-54-51
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NodeResourceInfo {

    private String nodeName;

    private String instance;

    private Double used;

    private Double total;
}
