



package com.flexspace.repository;

import com.flexspace.model.Booking;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Booking> bookingMapper = (rs, rowNum) -> {
        Booking b = new Booking();
        b.setId(rs.getLong("id"));
        b.setUserId(rs.getLong("user_id"));
        b.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        b.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        b.setStatus(rs.getString("status"));
        return b;
    };
    public BookingRepository (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate  = jdbcTemplate;
    }

    public int getHoursForDate(Long userId, LocalDate date){
        String sql = """
                SELECT COALESCE(SUM(TIMESTAMPDIFF(HOUR, start_time, end_time)), 0)
                FROM booking
                WHERE user_id = ?
                AND DATE(start_time) = ?
                AND status = 'CONFIRMED'    
                """;
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, date);
        return result != null ? result : 0;
    }



    public Booking save(Booking booking) {
        String sql = """
                INSERT INTO booking (user_id, start_time, end_time, status)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, booking.getUserId());
            ps.setTimestamp(2, Timestamp.valueOf(booking.getStartTime()));
            ps.setTimestamp(3, Timestamp.valueOf(booking.getEndTime()));
            ps.setString(4, booking.getStatus());
            return ps;
        }, keyHolder);

        booking.setId(keyHolder.getKey().longValue());
        return booking;
    }

    public List<Booking> findByUserId(Long userId) {
        String sql = """
        SELECT * FROM booking
        WHERE user_id = ?
        ORDER BY start_time DESC
    """;

        return jdbcTemplate.query(sql, bookingMapper, userId);
    }

    public List<Booking> findUpcomingByUserId(Long userId) {
        String sql = """
        SELECT * FROM booking
        WHERE user_id = ?
        AND start_time > NOW()
        AND status = 'CONFIRMED'
        ORDER BY start_time ASC
    """;

        return jdbcTemplate.query(sql, bookingMapper, userId);
    }

    public List<Booking> findPastByUserId(Long userId) {
        String sql = """
        SELECT * FROM booking
        WHERE user_id = ?
        AND start_time < NOW()
        ORDER BY start_time DESC
    """;

        return jdbcTemplate.query(sql, bookingMapper, userId);
    }

    public void updateStatus(Long id, String status) {
        String sql = "UPDATE booking SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    public Optional<Booking> findById(Long id) {
        String sql = "SELECT * FROM booking WHERE id = ?";

        List<Booking> list = jdbcTemplate.query(sql, bookingMapper, id);

        return list.stream().findFirst();
    }
}