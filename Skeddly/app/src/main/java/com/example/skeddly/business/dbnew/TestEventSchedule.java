package com.example.skeddly.business.dbnew;

import java.util.Objects;

public class TestEventSchedule {
    private long startTime;
    private long endTime;

    public TestEventSchedule() {

    }

    public TestEventSchedule(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestEventSchedule that = (TestEventSchedule) o;
        return startTime == that.startTime && endTime == that.endTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }
}
