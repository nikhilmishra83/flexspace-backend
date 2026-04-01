package com.flexspace.model;

import java.time.LocalDateTime;

public class DeskUnavailability {

    private Long id;
    private Long deskId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDeskId() { return deskId; }
    public void setDeskId(Long deskId) { this.deskId = deskId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}