package com.flexspace.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
    public boolean exists(List<Long> deskIds, LocalDateTime start, LocalDateTime end) {

        String placeholders = String.join(",", deskIds.stream().map(id -> "?").toList());

        String sql = """
        SELECT COUNT(*)
        FROM desk_unavailability
        WHERE desk_id IN (%s)
        AND start_time < ?
        AND end_time > ?
    """.formatted(placeholders);

        Object[] params = new Object[deskIds.size() + 2];

        for (int i = 0; i < deskIds.size(); i++) {
            params[i] = deskIds.get(i);
        }

        params[deskIds.size()] = end;
        params[deskIds.size() + 1] = start;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, params);

        return count != null && count > 0;
    }
}