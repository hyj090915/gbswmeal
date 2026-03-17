package com.gbsw.meal.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() throws Exception {
        String jdbcUrl = toJdbcUrl(databaseUrl);

        PGSimpleDataSource pgDs = new PGSimpleDataSource();
        pgDs.setURL(jdbcUrl);

        HikariConfig config = new HikariConfig();
        config.setDataSource(pgDs);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }

    private String toJdbcUrl(String url) {
        if (url.startsWith("postgres://")) {
            url = "jdbc:postgresql://" + url.substring("postgres://".length());
        } else if (url.startsWith("postgresql://")) {
            url = "jdbc:postgresql://" + url.substring("postgresql://".length());
        } else if (!url.startsWith("jdbc:")) {
            url = "jdbc:postgresql://" + url;
        }
        // channel_binding 파라미터 제거 (JDBC 드라이버 미지원)
        url = url.replaceAll("&channel_binding=[^&]*", "");
        url = url.replaceAll("\\?channel_binding=[^&]*&", "?");
        url = url.replaceAll("\\?channel_binding=[^&]*$", "");
        return url;
    }
}
