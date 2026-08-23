package com.fitness.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "database.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "database.replica")
    public DataSource replicaDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public DataSource routingDataSource() {
        DataSourceRouter router = new DataSourceRouter();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("PRIMARY", primaryDataSource());
        targetDataSources.put("REPLICA", replicaDataSource());

        router.setTargetDataSources(targetDataSources);
        router.setDefaultTargetDataSource(primaryDataSource()); // Default to Primary

        return router;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        // LazyConnection prevents Spring from getting a DB connection
        // BEFORE the transaction has started and the readOnly flag is set.
        return new LazyConnectionDataSourceProxy(routingDataSource());
    }
}