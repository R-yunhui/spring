package com.ral.young.boot.banner;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * 自定义Banner - 配置设置的 Banner > 代码输出的 Banner
 *
 * @author renyunhui
 * @date 2024-01-29 16:26
 * @since 1.0.0
 */
public class CustomBanner implements Banner {

    static final String bannerMsg = "::::.\n" +
            "                .::::::::.\n" +
            "               :::::::::::  FUCK YOU MAN\n" +
            "           ..:::::::::::'\n" +
            "         '::::::::::::'\n" +
            "           .::::::::::\n" +
            "      '::::::::::::::..\n" +
            "           ..::::::::::::.\n" +
            "         ``::::::::::::::::\n" +
            "          ::::``:::::::::'        .:::.\n" +
            "         ::::'   ':::::'       .::::::::.\n" +
            "       .::::'      ::::     .:::::::'::::.\n" +
            "      .:::'       :::::  .:::::::::' ':::::.\n" +
            "     .::'        :::::.:::::::::'      ':::::.\n" +
            "    .::'         ::::::::::::::'         ``::::.\n" +
            "...:::           ::::::::::::'              ``::.\n" +
            "``` ':.          ':::::::::'                  ::::..\n" +
            "                  '.:::::'                    ':'````..";

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        out.println(bannerMsg);
    }
}
