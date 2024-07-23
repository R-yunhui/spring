package com.ral.young.night.practice;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 删除指定目录下的指定后缀的文件
 *
 * @author renyunhui
 * @date 2024-07-17 16:58
 * @since 1.0.0
 */
@Slf4j
public class DeleteFileDemo {

    public static void main(String[] args) {
        deleteFile("D:\\renyunhui\\study\\doc\\极客时间", ".mp3");
        deleteFile("D:\\renyunhui\\study\\doc\\极客时间", ".pdf");
        deleteFile("D:\\renyunhui\\study\\doc\\极客时间", ".m4a");
    }

    public static void deleteFile(String filePath, String suffix) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    if (f.isFile()) {
                        String fileName = f.getName();
                        int idx = fileName.lastIndexOf(".");
                        if (idx != -1) {
                            String fileSuffix = fileName.substring(idx);
                            if (fileSuffix.equals(suffix)) {
                                FileUtil.del(f);
                                log.info("删除文件：{}", f.getAbsolutePath());
                            }
                        }
                    } else if (f.isDirectory()) {
                        deleteFile(f.getAbsolutePath(), suffix);
                    }
                }
            }
        }
        log.info("任务执行完成");
    }
}
