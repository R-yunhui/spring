package com.ral.young.study.designpattern.behavioral.observerpattern;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.util.List;

/**
 * 观察者 继承了Observer 接口
 * -- 也可以通过继承： jdk 自带的 Observer 接口简化代码
 *
 * @author renyunhui
 * @date 2023-12-04 15:39
 * @since 1.0.0
 */
public class WeChatUser implements Observer {

    private List<String> friendNameList;

    public WeChatUser() {
    }

    public WeChatUser(List<String> friendNameList) {
        this.friendNameList = friendNameList;
    }

    @Override
    public void update(Object arg) {
        DynamicInfo dynamicInfo = new DynamicInfo();
        if (arg instanceof DynamicInfo) {
            dynamicInfo = (DynamicInfo) arg;
        }
        for (String friendName : friendNameList) {
            System.out.println(friendName + "，你收到了一条动态消息，由" + dynamicInfo.getUsername() + "发布，发布时间：" + DateUtil.format(dynamicInfo.getPublichDate(), DatePattern.NORM_DATETIME_PATTERN));
        }
    }
}
