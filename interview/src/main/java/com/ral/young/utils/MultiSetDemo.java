package com.ral.young.utils;

import com.google.common.collect.HashMultiset;

/**
 * {@link com.google.common.collect.Multiset}
 *
 * @author renyunhui
 * @date 2022-10-12 15:05
 * @since 1.0.0
 */
public class MultiSetDemo {

    public static void main(String[] args) {
        HashMultiset<String> multiset = HashMultiset.create();
        multiset.add("a");
        multiset.add("a");
        multiset.add("a");
        multiset.add("b");
        multiset.add("c");
        // 统计字符串 a 的数量
        int aCount = multiset.count("a");
        System.out.println(aCount);

        // 统计集合中元素的数量 - 包含重复元素
        int size = multiset.size();
        System.out.println(size);
    }
}
