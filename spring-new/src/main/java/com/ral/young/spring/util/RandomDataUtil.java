package com.ral.young.spring.util;

import cn.hutool.core.util.RandomUtil;

import java.util.Set;

/**
 * 随机数据生成工具类
 */
public class RandomDataUtil {

    private static final String[] FIRST_NAMES = {"张", "李", "王", "赵", "钱", "孙", "周", "吴", "郑", "陈"};
    private static final String[] LAST_NAMES = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军"};
    private static final String[] EMAIL_SUFFIXES = {"@gmail.com", "@yahoo.com", "@hotmail.com", "@outlook.com", "@qq.com"};

    /**
     * 生成随机姓名
     */
    public static String generateRandomName() {
        String firstName = FIRST_NAMES[RandomUtil.randomInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[RandomUtil.randomInt(LAST_NAMES.length)];
        return firstName + lastName;
    }

    /**
     * 生成唯一邮箱
     */
    public static String generateUniqueEmail(Set<String> usedEmails) {
        String email;
        do {
            String prefix = RandomUtil.randomString(8).toLowerCase();
            String suffix = EMAIL_SUFFIXES[RandomUtil.randomInt(EMAIL_SUFFIXES.length)];
            email = prefix + suffix;
        } while (!usedEmails.add(email));

        return email;
    }
} 