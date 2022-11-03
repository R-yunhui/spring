package com.ral.young.concurrent.map;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link java.util.HashMap}
 *
 * @author renyunhui
 * @date 2022-09-27 9:53
 * @since 1.0.0
 */
public class HashMapDemo {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        // 不指定容量则默认为 16，指定了会返回 >= 指定容量且最接近的 2 的 n 次幂的值为容量
        Map<Integer, Integer> map = new HashMap<>(5);
        Field threshold = map.getClass().getDeclaredField("threshold");
        threshold.setAccessible(true);
        System.out.println(threshold.get(map));
    }
}
