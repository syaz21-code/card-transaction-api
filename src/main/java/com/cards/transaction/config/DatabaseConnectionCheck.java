
package com.cards.transaction.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionCheck implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseConnectionCheck(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {

        String databaseName = jdbcTemplate.queryForObject(
            "SELECT DB_NAME()",
            String.class
        );

        if (!"TESTDB".equals(databaseName)) {
            throw new IllegalStateException(
                "Connected to unexpected database: " + databaseName
            );
        }

        System.out.println(
            "DATABASE CONNECTION SUCCESSFUL: " + databaseName
        );
    }
}
