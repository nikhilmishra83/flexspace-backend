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