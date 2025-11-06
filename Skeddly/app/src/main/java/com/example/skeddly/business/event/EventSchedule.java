package com.example.skeddly.business.event;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class EventSchedule {
    private long startTime;
    private long endTime;

    // [SUN, MON, ..., FRI, SAT]
    private List<Boolean> daysOfWeek;
    private boolean isRecurring;

    public EventSchedule() {

    }

    public EventSchedule(LocalDateTime start, LocalDateTime end, Boolean[] daysOfWeek) {
        isRecurring = daysOfWeek != null;

        ZoneId zoneId = ZoneId.systemDefault();
        this.startTime = start.atZone(zoneId).toEpochSecond();
        this.endTime = end.atZone(zoneId).toEpochSecond();

        if (daysOfWeek != null) {
            this.daysOfWeek = Arrays.asList(daysOfWeek);
        }
    }

    public EventSchedule(LocalDateTime start, LocalDateTime end) {
        this(start, end, null);
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

    public List<Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<Boolean> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
}
