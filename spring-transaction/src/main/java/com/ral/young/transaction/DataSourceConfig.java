package com.ral.young.transaction;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 *
 * @author renyunhui
 * @date 2022-07-06 17:25
 * @since 1.0.0
 */
@Configuration
public class DataSourceConfig {

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://49.235.87.36:3306/study?serverTimezone=UTC");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("ryh123.0");
        return mysqlDataSource;
    }
}
