package com.flexspace.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long userId, Long planId, String status) {
        String sql = """
            INSERT INTO transaction (user_id, plan_id, status)
            VALUES (?, ?, ?)
        """;

        jdbcTemplate.update(sql, userId, planId, status);
    }
}