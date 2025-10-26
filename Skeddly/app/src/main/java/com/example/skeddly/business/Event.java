package com.example.skeddly.business;

import com.google.firebase.Timestamp;

public class Event extends DatabaseObject {
    public String name;
    public String description;
    public String category;
    public int attendeeLimit;
    public Long startTime;
    public Long endTime;
    public String owner;

}
