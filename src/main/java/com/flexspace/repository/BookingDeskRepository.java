package com.flexspace.repository;

import com.flexspace.model.BookingDesk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BookingDeskRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingDeskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsConflict(Long deskId, LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT COUNT(*)
            FROM booking_desk bd
            JOIN booking b ON bd.booking_id = b.id
            WHERE bd.desk_id = ?
            AND b.status = 'CONFIRMED'
            AND b.start_time < ?
            AND b.end_time > ?
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, deskId, end, start);
        return count != null && count > 0;
    }

    public boolean existsConflict(List<Long> deskIds, LocalDateTime start, LocalDateTime end) {

        String placeholders = String.join(",", deskIds.stream().map(id -> "?").toList());

        String sql = """
        SELECT COUNT(*)
        FROM booking_desk bd
        JOIN booking b ON bd.booking_id = b.id
        WHERE bd.desk_id IN (%s)
        AND b.status = 'CONFIRMED'
        AND b.start_time < ?
        AND b.end_time > ?
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

    public void save(BookingDesk bd) {
        String sql = """
            INSERT INTO booking_desk (booking_id, desk_id)
            VALUES (?, ?)
        """;

        jdbcTemplate.update(sql, bd.getBookingId(), bd.getDeskId());
    }
}