package com.ral.young.basic.tree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个TreeNode类
 * @date 2024-10-28 14-15-16
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreeNode {

    private Integer id;

    private Integer parentId;

    private String name;

    private Byte type;

    private List<TreeNode> children;
}
