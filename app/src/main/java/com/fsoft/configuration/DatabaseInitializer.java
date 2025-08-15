package com.fsoft.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Chạy triggers.sql khi application start
            executeSqlFile("triggers.sql");
            System.out.println("Database triggers initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing database triggers: " + e.getMessage());
        }
    }

    private void executeSqlFile(String filename) throws Exception {
        ClassPathResource resource = new ClassPathResource(filename);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String sql = reader.lines().collect(Collectors.joining("\n"));

            // Split by các statement riêng biệt và execute
            String[] statements = sql.split("(?<=;)\\s*(?=CREATE|DROP|DO|INSERT)");

            for (String statement : statements) {
                if (!statement.trim().isEmpty() && !statement.trim().startsWith("--")) {
                    jdbcTemplate.execute(statement.trim());
                }
            }
        }
    }
}
