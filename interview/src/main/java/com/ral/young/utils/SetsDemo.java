package com.ral.young.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * {@link com.google.common.collect.Sets}
 *
 * union(Set, Set) - 并集
 * intersection(Set, Set)  - 交集
 * difference(Set, Set)  - 差集
 * symmetricDifference(Set, Set)
 *
 * @author renyunhui
 * @date 2022-10-12 15:19
 * @since 1.0.0
 */
public class SetsDemo {

    public static void main(String[] args) {
        Set<String> setOne = ImmutableSet.of("a", "b", "c", "d");
        Set<String> setTwo = ImmutableSet.of("c", "d", "e", "f");

        Sets.SetView<String> union = Sets.union(setOne, setTwo);
        System.out.println("并集:" + union);

        Sets.SetView<String> intersection = Sets.intersection(setOne, setTwo);
        System.out.println("交集:" + intersection);

        Sets.SetView<String> difference = Sets.difference(setOne, setTwo);
        System.out.println("setOne 相对于 setTwo 的差集:" + difference);

        difference = Sets.difference(setTwo, setOne);
        System.out.println("setTwo 相对于 setOne 的差集:" + difference);

        Sets.SetView<String> strings = Sets.symmetricDifference(setOne, setTwo);
        System.out.println("差集:" + strings);
    }
}
