package com.flexspace.model;

public class Desk {

    private Long id;
    private Long spaceId;
    private int deskNumber;
    private String type;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSpaceId() { return spaceId; }
    public void setSpaceId(Long spaceId) { this.spaceId = spaceId; }

    public int getDeskNumber() { return deskNumber; }
    public void setDeskNumber(int deskNumber) { this.deskNumber = deskNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}