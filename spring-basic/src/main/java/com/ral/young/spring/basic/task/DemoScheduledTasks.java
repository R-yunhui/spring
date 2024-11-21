package com.ral.young.spring.basic.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class DemoScheduledTasks {

    /**
     * 模拟每5秒执行一次的用户数据统计任务
     */
    @Scheduled(fixedRate = 5000)
    public void userDataStatistics() {
        log.info("执行用户数据统计任务 - {}", LocalDateTime.now());
        // 模拟任务执行耗时
        sleep(2000);
    }

    /**
     * 模拟每10秒执行一次的系统监控任务
     */
    @Scheduled(fixedDelay = 10000)
    public void systemMonitor() {
        log.info("执行系统监控任务 - {}", LocalDateTime.now());
        sleep(3000);
    }

    /**
     * 模拟每分钟执行一次的数据备份任务
     */
    @Scheduled(cron = "0 * * * * ?")
    public void dataBackup() {
        log.info("执行数据备份任务 - {}", LocalDateTime.now());
        sleep(5000);
    }

    /**
     * 模拟每30秒执行一次的缓存清理任务
     */
    @Scheduled(fixedRate = 30000)
    public void cacheCleaner() {
        log.info("执行缓存清理任务 - {}", LocalDateTime.now());
        sleep(1000);
    }

    /**
     * 模拟每15秒执行一次的日志分析任务
     */
    @Scheduled(fixedDelay = 15000)
    public void logAnalysis() {
        log.info("执行日志分析任务 - {}", LocalDateTime.now());
        sleep(4000);
    }

    /**
     * 模拟每小时执行一次的报表生成任务
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void reportGeneration() {
        log.info("执行报表生成任务 - {}", LocalDateTime.now());
        sleep(6000);
    }

    /**
     * 模拟任务执行耗时
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}