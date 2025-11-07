package com.example.skeddly.business.event;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class EventSchedule {
    private long startTime;
    private long endTime;
    private long regStart;
    private long regEnd;

    // [SUN, MON, ..., FRI, SAT]
    private List<Boolean> daysOfWeek;
    private boolean isRecurring;

    public EventSchedule() {

    }

    public EventSchedule(LocalDateTime start, LocalDateTime end,
                         LocalDateTime regStart, LocalDateTime regEnd,
                         Boolean[] daysOfWeek) {
        isRecurring = daysOfWeek != null;

        ZoneId zoneId = ZoneId.systemDefault();
        this.startTime = start.atZone(zoneId).toEpochSecond();
        this.endTime = end.atZone(zoneId).toEpochSecond();
        this.regStart = regStart.atZone(zoneId).toEpochSecond();
        this.regEnd = regEnd.atZone(zoneId).toEpochSecond();

        if (daysOfWeek != null) {
            this.daysOfWeek = Arrays.asList(daysOfWeek);
        }
    }

    public EventSchedule(LocalDateTime start, LocalDateTime end,
                         LocalDateTime regStart, LocalDateTime regEnd) {
        this(start, end, regStart, regEnd, null);
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

    public long getRegStart() {
        return regStart;
    }

    public void setRegStart(long regStart) {
        this.regStart = regStart;
    }

    public long getRegEnd() {
        return regEnd;
    }

    public void setRegEnd(long regEnd) {
        this.regEnd = regEnd;
    }

    public boolean isRegistrationOver() {
        ZoneId zoneId = ZoneId.systemDefault();
        long curTime = LocalDateTime.now().atZone(zoneId).toEpochSecond();

        return curTime > regEnd;
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
