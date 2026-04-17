package com.flexspace.repository;

import com.flexspace.model.Desk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DeskRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void lockDeskById(Long deskId) {
        String sql = "SELECT id FROM desk WHERE id = ? FOR UPDATE";
        jdbcTemplate.queryForObject(sql, Long.class, deskId);
    }
    public void lockDeskById(List<Long> deskIds) {

        String placeholders = String.join(",", deskIds.stream().map(id -> "?").toList());

        String sql = "SELECT id FROM desk WHERE id IN (%s) FOR UPDATE".formatted(placeholders);

        jdbcTemplate.query(sql, rs -> {}, deskIds.toArray());
    }

    public Optional<Desk> findById(Long id) {
        String sql = "SELECT * FROM desk WHERE id = ?";

        List<Desk> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Desk d = new Desk();
            d.setId(rs.getLong("id"));
            d.setSpaceId(rs.getLong("space_id"));
            d.setDeskNumber(rs.getInt("desk_number"));
            d.setType(rs.getString("type"));
            return d;
        }, id);

        return list.stream().findFirst();
    }
}