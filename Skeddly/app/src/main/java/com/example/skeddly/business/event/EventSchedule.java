package com.example.skeddly.business.event;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * An event schedule that can be serialized into the DB
 */
public class EventSchedule {
    private long startTime;
    private long endTime;
    private long regStart;
    private long regEnd;

    // [SUN, MON, ..., FRI, SAT]
    private List<Boolean> daysOfWeek;
    private boolean isRecurring;

    /**
     * No arg Constructor for the EventSchedule
     */
    public EventSchedule() {

    }

    /**
     * Constructor for the EventSchedule
     * @param start The start time of the event
     * @param end The end time of the event
     * @param daysOfWeek The days of the week the event occurs on
     */
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

    /**
     * Constructor for the EventSchedule without days of week
     * @param start The start time of the event
     * @param end The end time of the event
     */
    public EventSchedule(LocalDateTime start, LocalDateTime end,
                         LocalDateTime regStart, LocalDateTime regEnd) {
        this(start, end, regStart, regEnd, null);
    }

    /**
     * Gets the start time of the event
     * @return The start time of the event
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the event
     * @param startTime The start time of the event
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time of the event
     * @return The end time of the event
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the event
     * @param endTime The end time of the event
     */
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

    /**
     * Gets the days of the week the event occurs on
     * @return The days of the week the event occurs on
     */
    public List<Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }

    /**
     * Sets the days of the week the event occurs on
     * @param daysOfWeek The days of the week the event occurs on
     */
    public void setDaysOfWeek(List<Boolean> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    /**
     * Returns if the event is recurring
     * @return If the event is recurring
     */
    public boolean isRecurring() {
        return isRecurring;
    }

    /**
     * Sets if the event is recurring
     * @param recurring If the event is recurring
     */
    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
}
