package com.gbsw.meal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() throws Exception {
        // postgresql://user:pass@host/db?params → JDBC 분리 파싱
        String raw = databaseUrl;
        if (raw.startsWith("postgres://")) {
            raw = "http://" + raw.substring("postgres://".length());
        } else if (raw.startsWith("postgresql://")) {
            raw = "http://" + raw.substring("postgresql://".length());
        } else if (raw.startsWith("jdbc:postgresql://")) {
            raw = "http://" + raw.substring("jdbc:postgresql://".length());
        }

        URI uri = new URI(raw);
        String host = uri.getHost();
        int port = uri.getPort(); // -1 if not specified
        String path = uri.getPath(); // /dbname

        String username = null, password = null;
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            String[] parts = userInfo.split(":", 2);
            username = parts[0];
            password = parts.length > 1 ? parts[1] : null;
        }

        String jdbcUrl = "jdbc:postgresql://" + host
                + (port > 0 ? ":" + port : "")
                + path
                + "?sslmode=require";

        System.out.println("[DataSourceConfig] host=" + host + " db=" + path + " user=" + username);

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(jdbcUrl);
        if (username != null) ds.setUsername(username);
        if (password != null) ds.setPassword(password);
        return ds;
    }
}
