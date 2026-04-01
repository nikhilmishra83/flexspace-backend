package com.flexspace.repository;

import com.flexspace.model.Plan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlanRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Plan> findAll() {
        String sql = "SELECT * FROM plan";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Plan p = new Plan();
            p.setId(rs.getLong("id"));
            p.setName(rs.getString("name"));
            p.setDurationType(rs.getString("duration_type"));
            p.setDurationValue(rs.getInt("duration_value"));
            p.setDailyBookingHours(rs.getInt("daily_booking_hours"));
            p.setPrice(rs.getBigDecimal("price"));
            return p;
        });
    }

    public Plan findById(Long id) {
        String sql = "SELECT * FROM plan WHERE id = ?";

        List<Plan> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Plan p = new Plan();
            p.setId(rs.getLong("id"));
            p.setName(rs.getString("name"));
            p.setDurationType(rs.getString("duration_type"));
            p.setDurationValue(rs.getInt("duration_value"));
            p.setDailyBookingHours(rs.getInt("daily_booking_hours"));
            p.setPrice(rs.getBigDecimal("price"));
            return p;
        }, id);

        return list.isEmpty() ? null : list.get(0);
    }
}