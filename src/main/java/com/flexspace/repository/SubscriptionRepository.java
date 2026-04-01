package com.flexspace.repository;

import com.flexspace.model.Subscription;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SubscriptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Subscription> mapper = (rs, rowNum) -> {
        Subscription s = new Subscription();
        s.setId(rs.getLong("id"));
        s.setUserId(rs.getLong("user_id"));
        s.setPlanId(rs.getLong("plan_id"));
        s.setStartDate(rs.getDate("start_date").toLocalDate());
        s.setEndDate(rs.getDate("end_date").toLocalDate());
        s.setStatus(rs.getString("status"));
        return s;
    };

    public List<Subscription> findActiveSubscriptions(Long userId) {
        String sql = """
            SELECT * FROM subscription
            WHERE user_id = ?
            AND status = 'ACTIVE'
        """;

        return jdbcTemplate.query(sql, mapper, userId);
    }

    public int getTotalDailyHours(Long userId, LocalDate date) {
        String sql = """
            SELECT COALESCE(SUM(p.daily_booking_hours), 0)
            FROM subscription s
            JOIN plan p ON s.plan_id = p.id
            WHERE s.user_id = ?
            AND s.status = 'ACTIVE'
            AND s.start_date <= ?
            AND s.end_date >= ?
        """;

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, date, date);
        return result != null ? result : 0;
    }
}