package com.ral.young.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * {@link com.google.common.collect.Multimap}
 *
 * @author renyunhui
 * @date 2022-10-12 15:15
 * @since 1.0.0
 */
public class MultiMapDemo {

    public static void main(String[] args) {
        // 类似 Map<String , List<String>>
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("a", "test1");
        multimap.put("a", "test2");
        multimap.put("a", "test3");
        multimap.put("b", "test4");
        // 获取到的是一个集合
        Collection<String> collection = multimap.get("a");
        System.out.println(collection);

        Collection<String> values = multimap.values();
        System.out.println(values);
    }
}
