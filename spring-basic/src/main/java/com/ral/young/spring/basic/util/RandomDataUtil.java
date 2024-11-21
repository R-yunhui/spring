package com.ral.young.spring.basic.util;

import cn.hutool.core.util.RandomUtil;
import com.ral.young.spring.basic.entity.User;
import com.ral.young.spring.basic.enums.CommonEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 随机数据生成工具类
 */
public class RandomDataUtil {
    
    private static final String[] FIRST_NAMES = {"张", "李", "王", "赵", "钱", "孙", "周", "吴", "郑", "陈"};
    private static final String[] LAST_NAMES = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军"};
    private static final String[] EMAIL_SUFFIXES = {"@gmail.com", "@yahoo.com", "@hotmail.com", "@outlook.com", "@qq.com"};
    
    /**
     * 生成随机用户数据
     *
     * @param count 生成数量
     * @return 用户数据列表
     */
    public static List<User> generateRandomUsers(int count) {
        List<User> users = new ArrayList<>(count);
        Set<String> usedEmails = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            User user = new User();
            // 生成姓名
            user.setName(generateRandomName());
            // 生成年龄 (18-60)
            user.setAge(RandomUtil.randomInt(18, 61));
            // 生成性别
            int genderValue = RandomUtil.randomInt(0, 3);
            user.setGender(CommonEnum.GenderEnum.fromValue(genderValue));
            // 生成唯一邮箱
            user.setEmail(generateUniqueEmail(usedEmails));

            user.setCreateUser(1L);
            user.setUpdateUser(1L);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            users.add(user);
        }
        
        return users;
    }
    
    /**
     * 生成随机姓名
     */
    private static String generateRandomName() {
        String firstName = FIRST_NAMES[RandomUtil.randomInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[RandomUtil.randomInt(LAST_NAMES.length)];
        return firstName + lastName;
    }
    
    /**
     * 生成唯一邮箱
     */
    private static String generateUniqueEmail(Set<String> usedEmails) {
        String email;
        do {
            String prefix = RandomUtil.randomString(8).toLowerCase();
            String suffix = EMAIL_SUFFIXES[RandomUtil.randomInt(EMAIL_SUFFIXES.length)];
            email = prefix + suffix;
        } while (!usedEmails.add(email));
        
        return email;
    }
} 