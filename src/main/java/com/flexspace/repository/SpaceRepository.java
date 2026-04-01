package com.flexspace.repository;

import com.flexspace.model.CoworkingSpace;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SpaceRepository {

    private final JdbcTemplate jdbcTemplate;

    public SpaceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CoworkingSpace> findAll() {
        String sql = "SELECT * FROM coworking_space";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CoworkingSpace s = new CoworkingSpace();
            s.setId(rs.getLong("id"));
            s.setName(rs.getString("name"));
            s.setOwnerId(rs.getLong("owner_id"));
            s.setAddress(rs.getString("address"));
            s.setCity(rs.getString("city"));
            s.setState(rs.getString("state"));
            return s;
        });
    }

    public List<CoworkingSpace> findByCity(String city) {
        String sql = "SELECT * FROM coworking_space WHERE city = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CoworkingSpace s = new CoworkingSpace();
            s.setId(rs.getLong("id"));
            s.setName(rs.getString("name"));
            s.setOwnerId(rs.getLong("owner_id"));
            s.setAddress(rs.getString("address"));
            s.setCity(rs.getString("city"));
            s.setState(rs.getString("state"));
            return s;
        }, city);
    }
}