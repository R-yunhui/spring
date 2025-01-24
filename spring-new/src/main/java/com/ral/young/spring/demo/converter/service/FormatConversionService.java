package com.ral.young.spring.demo.converter.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.ral.young.spring.demo.converter.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FormatConversionService {

    private static final long LARGE_FILE_THRESHOLD = 1024 * 1024 * 10; // 10MB
    private static final long SHUTDOWN_TIMEOUT = 5; // 默认等待5分钟
    private final ConvertConfig defaultConfig;
    private final ThreadPoolExecutor executor;
    private final LargeFileConverter largeFileConverter;

    public FormatConversionService(int threadPoolSize) {
        this.executor = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000), ThreadFactoryBuilder.create().setNamePrefix("dataset-convert-").build(), new ThreadPoolExecutor.CallerRunsPolicy());

        this.largeFileConverter = new LargeFileConverter();

        // 初始化默认配置
        Map<String, String> defaultMapping = new HashMap<>();
        defaultMapping.put("小汽车", "car");
        this.defaultConfig = ConvertConfig.builder().labelCodeMapping(defaultMapping).build();
    }

    public void batchConvert(String inputDir, String outputDir, boolean isJsonToXml) {
        File dir = new File(inputDir);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("输入路径必须是目录");
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(isJsonToXml ? ".json" : ".xml"));

        if (files != null) {
            Arrays.stream(files).forEach(file -> executor.submit(() -> convertFile(file, outputDir, isJsonToXml)));
        }
    }

    private void convertFile(File inputFile, String outputDir, boolean isJsonToXml) {
        try {
            String newFileName = inputFile.getName().replace(isJsonToXml ? ".json" : ".xml", isJsonToXml ? ".xml" : ".json");

            // 检查文件大小
            if (inputFile.length() > LARGE_FILE_THRESHOLD) {
                log.info("检测到大文件：{}，使用流式处理", inputFile.getName());

                File outputFile = new File(outputDir, newFileName);
                largeFileConverter.processLargeFile(inputFile, outputFile, isJsonToXml);
            } else {
                // 小文件处理逻辑
                String content = FileUtil.readString(inputFile, StandardCharsets.UTF_8);
                FormatConverter converter = isJsonToXml ? new JsonToXmlConverter() : new XmlToJsonConverter();
                String result = convertWithRetry(content, converter, defaultConfig);
                File outputFile = new File(outputDir, newFileName);
                FileUtil.writeString(result, outputFile, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("转换文件失败: {}", inputFile.getName(), e);
        }
    }

    private String convertWithRetry(String input, FormatConverter converter, ConvertConfig config) {
        Exception lastException = null;
        for (int i = 0; i < config.getRetryTimes(); i++) {
            try {
                return converter.convert(input, config);
            } catch (Exception e) {
                lastException = e;
                log.warn("转换失败，第{}次重试", i + 1, e);
                // 添加重试间隔
                try {
                    TimeUnit.MILLISECONDS.sleep(100L * (i + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ConversionException("转换被中断", ie);
                }
            }
        }
        throw new ConversionException("转换失败，已重试" + config.getRetryTimes() + "次", lastException);
    }

    /**
     * 关闭服务
     * @param waitForTasks 是否等待所有任务完成
     */
    public void shutdown(boolean waitForTasks) {
        try {
            if (waitForTasks) {
                log.info("shutdown 等待所有转换任务完成...");
                // 停止接收新任务，但继续处理队列中的任务
                executor.shutdown();
                // 等待所有任务完成或超时
                if (!executor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MINUTES)) {
                    log.warn("等待任务完成超时，强制关闭转换服务");
                    executor.shutdownNow();
                }
            } else {
                log.info("立即停止所有转换任务");
                // 立即中断所有任务
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("关闭转换服务时被中断", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 等待所有任务完成
     * @param timeout 超时时间（分钟）
     * @return 是否所有任务都完成
     */
    public boolean awaitCompletion(long timeout) {
        try {
            log.info("awaitCompletion 等待所有转换任务完成...");
            while (timeout > 0) {
                if (executor.getActiveCount() == 0 && executor.getQueue().isEmpty()) {
                    log.info("所有转换任务已完成");
                    return true;
                }
                log.info("当前活跃任务数: {}, 队列中任务数: {}", executor.getActiveCount(), executor.getQueue().size());
                Thread.sleep(5000); // 每5秒检查一次
                timeout -= 5000;
            }
            log.warn("等待任务完成超时");
            return false;
        } catch (InterruptedException e) {
            log.error("等待任务完成时被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 检查服务是否正在关闭
     */
    public boolean isShuttingDown() {
        return executor.isShutdown() || executor.isTerminating();
    }

    /**
     * 获取当前活跃的转换任务数
     */
    public int getActiveTaskCount() {
        return executor.getActiveCount();
    }

    /**
     * 获取队列中等待的任务数
     */
    public int getQueuedTaskCount() {
        return executor.getQueue().size();
    }
} 