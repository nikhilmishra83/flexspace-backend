package com.flexspace.repository;

import com.flexspace.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";

        List<User> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setName(rs.getString("name"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole(rs.getString("role"));
            u.setActive(rs.getBoolean("active"));
            return u;
        }, email);

        return list.stream().findFirst();
    }

    // ✅ FIXED: Return Long instead of int
    public User save(User user) {
        String sql = """
        INSERT INTO user (name, email, password, role, active)
        VALUES (?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isActive());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        user.setId( key != null ? key.longValue() : null);

        return user;
    }

}