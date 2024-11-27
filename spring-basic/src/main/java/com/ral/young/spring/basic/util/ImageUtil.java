package com.ral.young.spring.basic.util;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.ral.young.spring.basic.dto.BoxInfoDTO;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author renyunhui
 * @description 这是一个ImageUtil类
 * @date 2024-11-27 14-51-40
 * @since 1.0.0
 */
@Slf4j
public class ImageUtil {

    private static Font FONT;

    static {
        try {
            FONT = loadLocalFont();
        } catch (RuntimeException e) {
            // 记录错误日志
            FONT = new Font("Arial", Font.PLAIN, 12); // 使用系统默认字体
        }
    }

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch("task");
        stopWatch.start();
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            IntStream.range(1, 100).forEach(i -> {
                String imgUrl = "https://tse3-mm.cn.bing.net/th/id/OIP-C.M8iHHkcmCefkAJcBfsbaSwHaE8?w=260&h=180&c=7&r=0&o=5&pid=1.7";
                List<BoxInfoDTO> boxInfoDTOS = Lists.newArrayList(
                        BoxInfoDTO.builder().x(100).y(100).width(200).height(200).text("测试文本").textColor("ff0000").boxColor("ff0000").fontSize(20).build()
                );
                processImage(imgUrl, boxInfoDTOS, "C:\\Users\\Administrator\\Desktop\\test");
            });
        });

        voidCompletableFuture.join();
        stopWatch.stop();
        log.info("总耗时:{}", stopWatch.getTotalTimeSeconds());
    }

    /**
     * 处理图片，绘制框及对应的字体
     * @param imgUrl 图片地址
     * @param boxInfoDTOS 框及对应的字体信息
     * @param outputPath 输出地址
     */
    public static void processImage(String imgUrl, List<BoxInfoDTO> boxInfoDTOS, String outputPath) {
        Graphics2D g2d = null;
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start("Read Image");
            // 读取原始图片
            BufferedImage originalImage = readImageFromUrl(imgUrl);
            stopWatch.stop();

            stopWatch.start("Create Graphics");
            // 创建图形上下文
            g2d = createGraphics(originalImage);
            stopWatch.stop();

            stopWatch.start("Draw Boxes and Text");
            // 绘制所有框和文字
            drawBoxesAndText(g2d, originalImage, boxInfoDTOS);
            stopWatch.stop();

            stopWatch.start("Save Image");
            // 保存处理后的图片
            saveImage(originalImage, outputPath);
            stopWatch.stop();

        } catch (IOException e) {
            log.error("处理图片失败，imgUrl={}, outputPath={}, errorMsg={}", imgUrl, outputPath, e.getMessage(), e);
            throw new RuntimeException("处理图片失败: " + e.getMessage());
        } finally {
            if (g2d != null) {
                g2d.dispose();
            }
            log.info("处理图片各步骤耗时: {}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        }
    }

    /**
     * 从URL读取图片
     */
    private static BufferedImage readImageFromUrl(String imgUrl) throws IOException {
        URL imageUrl = new URL(imgUrl);
        java.net.URLConnection conn = imageUrl.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        BufferedImage originalImage = ImageIO.read(conn.getInputStream());
        if (originalImage == null) {
            log.error("无法读取图片，图片地址：{}", imgUrl);
            throw new IOException("无法读取图片");
        }
        return originalImage;
    }

    /**
     * 创建并配置Graphics2D对象
     */
    private static Graphics2D createGraphics(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g2d;
    }

    /**
     * 绘制所有框和文字
     */
    private static void drawBoxesAndText(Graphics2D g2d, BufferedImage image, List<BoxInfoDTO> boxInfoDTOS) {
        for (BoxInfoDTO boxInfo : boxInfoDTOS) {
            if (boxInfo == null) {
                continue;
            }
            drawSingleBoxAndText(g2d, image, boxInfo);
        }
    }

    /**
     * 绘制单个框和文字
     */
    private static void drawSingleBoxAndText(Graphics2D g2d, BufferedImage image, BoxInfoDTO boxInfo) {
        // 获取颜色
        Color boxColor = getColor(boxInfo.getBoxColor(), Color.RED);
        Color textColor = getColor(boxInfo.getTextColor(), Color.BLACK);

        // 计算有效坐标
        int x = Math.max(0, Math.min(boxInfo.getX(), image.getWidth()));
        int y = Math.max(0, Math.min(boxInfo.getY(), image.getHeight()));
        int width = Math.min(boxInfo.getWidth(), image.getWidth() - x);
        int height = Math.min(boxInfo.getHeight(), image.getHeight() - y);

        // 绘制框
        g2d.setColor(boxColor);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x, y, width, height);

        // 绘制文字
        if (StrUtil.isNotBlank(boxInfo.getText())) {
            g2d.setFont(FONT.deriveFont(Font.PLAIN, Optional.of(boxInfo.getFontSize()).orElse(12)));
            g2d.setColor(textColor);
            g2d.drawString(boxInfo.getText(), x + 5, y - 5);
        }
    }

    /**
     * 解析颜色值
     */
    private static Color getColor(String colorHex, Color defaultColor) {
        try {
            return StrUtil.isNotBlank(colorHex) ? new Color(Integer.parseInt(colorHex, 16)) : defaultColor;
        } catch (NumberFormatException e) {
            log.warn("颜色格式错误，使用默认颜色: {}", colorHex);
            return defaultColor;
        }
    }

    /**
     * 保存图片到指定路径
     */
    private static void saveImage(BufferedImage image, String outputPath) throws IOException {
        String imgName = IdUtil.fastSimpleUUID();
        String path = outputPath + "\\" + imgName + ".png";
        File outputFile = new File(path);
        if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
            log.error("创建输出目录失败，outputPath={}", path);
            throw new IOException("创建输出目录失败");
        }
        ImageIO.write(image, "png", outputFile);
    }

    private static Font loadLocalFont() {
        try (InputStream is = ImageUtil.class.getClassLoader().getResourceAsStream("fonts/SimSun.ttf")) {
            if (is != null) {
                return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 20);
            } else {
                log.error("字体文件未找到: {}", "fonts/SimSun.ttf");
                return new Font("SimSun", Font.PLAIN, 20); // 使用系统默认的中文字体
            }
        } catch (Exception e) {
            log.error("加载字体失败,使用系统默认字体: {}", e.getMessage());
            return new Font("SimSun", Font.PLAIN, 20); // 使用系统默认的中文字体
        }
    }
}
