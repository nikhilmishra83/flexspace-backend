package com.flexspace.dto;

import com.flexspace.model.Booking;

import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.userId = booking.getUserId();
        this.startTime = booking.getStartTime();
        this.endTime = booking.getEndTime();
        this.status = booking.getStatus();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
}