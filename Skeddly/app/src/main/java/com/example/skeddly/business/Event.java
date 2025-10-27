package com.example.skeddly.business;

import com.example.skeddly.business.database.DatabaseObject;

public class Event extends DatabaseObject {
    private String name;
    private String description;
    private String category;
    private int attendeeLimit;
    private Long startTime;
    private Long endTime;
    private String owner;

    public Event() {
        // Auto filled by Firebase DB

        this.name = "";
        this.description = "";
        this.category = "";
        this.attendeeLimit = 20;
        this.startTime = (long) 0;
        this.endTime = (long) 0;
        this.owner = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAttendeeLimit() {
        return attendeeLimit;
    }

    public void setAttendeeLimit(int attendeeLimit) {
        this.attendeeLimit = attendeeLimit;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
