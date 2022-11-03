package com.ral.young.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * {@link com.google.common.collect.BiMap}
 *
 * @author renyunhui
 * @date 2022-10-12 15:29
 * @since 1.0.0
 */
public class BiMapDemo {

    public static void main(String[] args) {
        /*
         * BiMap<K, V>是特殊的Map：
         *
         * 可以用 inverse()反转BiMap<K, V>的键值映射
         * 保证值是唯一的，因此 values()返回Set而不是普通的Collection
         */
        BiMap<String, String> biMap = HashBiMap.create();
        biMap.put("a", "test1");
        biMap.put("b", "test2");
        biMap.put("c", "test3");
        System.out.println("key - value:" + biMap.get("a"));
        System.out.println("value - key:" + biMap.inverse().get("test1"));
    }
}
