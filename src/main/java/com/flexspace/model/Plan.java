package com.flexspace.model;

import java.math.BigDecimal;

public class Plan {

    private Long id;
    private String name;
    private String durationType;   // DAY / MONTH
    private Integer durationValue;
    private Integer dailyBookingHours;
    private BigDecimal price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDurationType() { return durationType; }
    public void setDurationType(String durationType) { this.durationType = durationType; }

    public Integer getDurationValue() { return durationValue; }
    public void setDurationValue(Integer durationValue) { this.durationValue = durationValue; }

    public Integer getDailyBookingHours() { return dailyBookingHours; }
    public void setDailyBookingHours(Integer dailyBookingHours) { this.dailyBookingHours = dailyBookingHours; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}