package com.ral.young.night.project.filter;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author renyunhui
 * @date 2023-12-13 15:32
 * @since 1.0.0
 */
// @Component(value = "statFilter")
@Slf4j
public class MyStatFilter extends StatFilter {

    @Override
    protected void handleSlowSql(StatementProxy statementProxy) {
        // 处理慢sql
        final String slowSql = statementProxy.getLastExecuteSql();
        final long nowNano = System.nanoTime();
        final long nanos = nowNano - statementProxy.getLastExecuteStartNano();
        long consumingTime = nanos / (1000 * 1000);
        // 超过 1000 ms 算慢 sql
       if (consumingTime > 1000) {
           log.info("慢sql：{}，耗时：{}ms", slowSql, consumingTime);
       }
    }
}
