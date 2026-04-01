package com.flexspace.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class DeskUnavailabilityRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeskUnavailabilityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean exists(Long deskId, LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT COUNT(*)
            FROM desk_unavailability
            WHERE desk_id = ?
            AND start_time < ?
            AND end_time > ?
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, deskId, end, start);
        return count != null && count > 0;
    }
}