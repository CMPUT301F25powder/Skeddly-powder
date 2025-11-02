package com.example.skeddly.business;

import android.location.Location;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.user.User;

import java.time.LocalDateTime;

public class Event extends DatabaseObject {
    private String name;
    private String description;
    private String category;
    private long startTime;
    private long endTime;
    private CustomLocation location;
    private String organizer;
    private WaitingList applicants;
    private ParticipantList attendees;

    public Event() {
        // Auto filled by Firebase DB

        //this.name = "";
        //this.description = "";
        //this.category = "";
        //this.startTime = (LocalDateTime) 0;
        //this.endTime = (LocalDateTime) 0;
        //this.owner = "";
        //applicants = new WaitingList();
        //attendees = new ParticipantList(20);

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
        return attendees.getMaxAttend();
    }

    public void setAttendeeLimit(int attendeeLimit) {
        this.attendees.setMaxAttend(attendeeLimit);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public void setLocation(CustomLocation location) {
        this.location = location;
    }

    public WaitingList getApplicants() {
        return applicants;
    }

    public void setApplications(WaitingList applicants) {
        this.applicants = applicants;
    }

    public ParticipantList getAttendees() {
        return attendees;
    }

    public void setAttendees(ParticipantList attendees) {
        this.attendees = attendees;
    }
}
