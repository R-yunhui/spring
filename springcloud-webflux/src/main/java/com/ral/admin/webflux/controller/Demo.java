package com.ral.admin.webflux.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @Author: RenYunHui
 * @Date: 2021-05-11 16:54
 * @Describe:
 * @Modify:
 */
@Slf4j
public class Demo {

    @Data
    @AllArgsConstructor
    private static class User {
        private int userId;

        private String username;

        private int gender;
    }

    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "mike1", 1));
        users.add(new User(1, "mike2", 1));
        users.add(new User(1, "mike3", 2));
        users.add(new User(2, "mike4", 2));
        users.add(new User(2, "mike5", 1));
        users.add(new User(3, "mike6", 1));
        users.add(new User(4, "mike7", 1));
        log.info("去重前：" + users);
        users = users.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getUserId() + ";" + o.getGender()))), ArrayList::new));
        log.info("根据ID+性别去重后：" + users);
    }
}
