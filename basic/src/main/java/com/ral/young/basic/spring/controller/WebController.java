package com.ral.young.basic.spring.controller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author renyunhui
 * @date 2023-06-02 10:06
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/web")
public class WebController {

    @GetMapping(value = "/test/{num}")
    public void test(@PathVariable(value = "num") int num) {
        if (num == 1) {
            System.out.println("num：" + num);
            Map<String, Integer> map = new HashMap<>(16);
            map.put("num", num);
            System.out.println("map:" + map.size());
        } else if (num == 2) {
            System.out.println("num：" + num);
            List<Integer> list = new ArrayList<>();
            list.add(num);
            System.out.println("map:" + list.size());
        } else {
            System.out.println("num：" + num);
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
}
