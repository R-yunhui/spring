package com.ral.young.basic.tree;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author renyunhui
 * @description 这是一个Demo类
 * @date 2024-10-28 14-16-11
 * @since 1.0.0
 */
public class Demo {

    public static void main(String[] args) {
        // 模拟构建树
        List<TreeNode> treeNodes = getTreeNodes();
        TreeNode treeNode = buildTree(treeNodes);
        System.out.println("===== 构建好的树 =====");
        System.out.println(JSONUtil.toJsonPrettyStr(treeNode));

        System.out.println("===== 过滤后的树 =====");
        ArrayList<TreeNode> nodes = Lists.newArrayList(treeNode);
        filter(nodes);
        System.out.println(JSONUtil.toJsonPrettyStr(nodes));
    }

    private static ArrayList<TreeNode> getTreeNodes() {
        TreeNode node111 = new TreeNode();
        node111.setId(1111);
        node111.setName("三级节点-01-01-01");
        node111.setType((byte) 2);
        node111.setParentId(111);

        TreeNode node11 = new TreeNode();
        node11.setId(111);
        node11.setName("二级节点-01-01");
        node11.setType((byte) 1);
        node11.setParentId(11);

        TreeNode node12 = new TreeNode();
        node12.setId(112);
        node12.setName("二级节点-01-02");
        node12.setType((byte) 2);
        node12.setParentId(11);

        TreeNode node13 = new TreeNode();
        node13.setId(113);
        node13.setName("二级节点-01-03");
        node13.setType((byte) 1);
        node13.setParentId(11);

        TreeNode node14 = new TreeNode();
        node14.setId(114);
        node14.setName("二级节点-01-04");
        node14.setType((byte) 1);
        node14.setParentId(11);

        TreeNode node1 = new TreeNode();
        node1.setId(11);
        node1.setName("一级节点-01");
        node1.setType((byte) 1);
        node1.setParentId(-1);

        TreeNode node21 = new TreeNode();
        node21.setId(211);
        node21.setName("二级节点-02-01");
        node21.setType((byte) 2);
        node21.setParentId(12);

        TreeNode node22 = new TreeNode();
        node22.setId(212);
        node22.setName("二级节点-02-02");
        node22.setType((byte) 2);
        node22.setParentId(12);

        TreeNode node23 = new TreeNode();
        node23.setId(213);
        node23.setName("二级节点-02-03");
        node23.setType((byte) 2);
        node23.setParentId(12);

        TreeNode node24 = new TreeNode();
        node24.setId(214);
        node24.setName("二级节点-02-04");
        node24.setType((byte) 2);
        node24.setParentId(12);

        TreeNode node2 = new TreeNode();
        node2.setId(12);
        node2.setName("一级节点-02");
        node2.setType((byte) 1);
        node2.setParentId(-1);

        TreeNode node3 = new TreeNode();
        node3.setId(13);
        node3.setName("一级节点-03");
        node3.setType((byte) 1);
        node3.setParentId(-1);

        TreeNode node4 = new TreeNode();
        node4.setId(14);
        node4.setName("一级节点-04");
        node4.setType((byte) 2);
        node4.setParentId(-1);

        TreeNode root = new TreeNode();
        root.setId(-1);
        root.setName("根节点");
        root.setType((byte) 1);
        root.setParentId(0);
        // return CollUtil.newArrayList(node111, node11, node12, node13, node14, node1, node21, node22, node23, node24, node2, node3, node4, root);
        return CollUtil.newArrayList(node11, node1, node2, node3, root);
    }

    public static TreeNode buildTree(List<TreeNode> treeNodes) {
        Map<Integer, List<TreeNode>> treeMap = treeNodes.stream().collect(Collectors.groupingBy(TreeNode::getParentId));
        treeNodes.forEach(o -> {
            Integer id = o.getId();
            List<TreeNode> nodes = treeMap.get(id);
            if (CollUtil.isNotEmpty(nodes)) {
                o.setChildren(nodes);
            }
        });
        return treeNodes.stream().filter(o -> o.getParentId() == 0).findFirst().get();
    }

    public static void filter(List<TreeNode> nodeList) {
        if (CollUtil.isEmpty(nodeList)) {
            return;
        }

        Iterator<TreeNode> iterator = nodeList.iterator();
        while (iterator.hasNext()) {
            TreeNode treeNode = filterNode(iterator.next());
            // 如果过滤完成之后只剩下根节点了也需要进行过滤
            if (treeNode.getType() == 1 && CollUtil.isEmpty(treeNode.getChildren())) {
                iterator.remove();
            }
        }
    }

    public static TreeNode filterNode(TreeNode node) {
        // 如果节点类型为 1 且子节点为空，则过滤即可
        if (null == node) {
            return null;
        }

        List<TreeNode> children = node.getChildren();
        if (CollUtil.isEmpty(children)) {
            return node;
        }

        Iterator<TreeNode> iterator = children.iterator();
        while (iterator.hasNext()) {
            TreeNode cur = iterator.next();
            filterNode(cur);

            if (cur.getType() == 1 && CollUtil.isEmpty(cur.getChildren())) {
                iterator.remove();
            }
        }
        return node;
    }
}
