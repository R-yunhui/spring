package com.ral.young.basic.snapshot;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author renyunhui
 * @description 这是一个SnapShotService类
 * @date 2024-10-29 16-40-28
 * @since 1.0.0
 */
@Service
@Slf4j
public class SnapShotService {

    @Resource
    private RestTemplate restTemplate;

    public void test(String rtspUrl) {
        String path = System.getProperty("user.dir") + "/" + IdUtil.fastSimpleUUID() + ".png";
        try {
            if (StrUtil.isBlank(rtspUrl)) {
                rtspUrl = "rtsp://182.140.132.195/lv_0_20241010184610.mp4";
            }
            snapshot(rtspUrl, path);
        } finally {
            if (FileUtil.exist(path)) {
                FileUtil.del(path);
            }
        }
    }

    public static void snapshot(String rtspUrl, String outputPath) {
        String command = "ffmpeg -rtsp_transport tcp -i %s -frames:v 1 %s";
        String format = String.format(command, rtspUrl, outputPath);

        try {
            Process process = Runtime.getRuntime().exec(format);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("截图成功！");
            } else {
                System.err.println("截图失败，FFmpeg 返回码：" + exitCode);
                // 获取错误输出
                BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = errorStream.readLine()) != null) {
                    System.err.println(line);
                }
            }

        } catch (Exception e) {
            log.error("通过 ffmpeg 命令截图 rtsp 流失败:", e);
        }
    }
}
