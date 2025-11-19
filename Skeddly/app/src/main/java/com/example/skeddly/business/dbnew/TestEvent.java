package com.example.skeddly.business.dbnew;

import java.util.Objects;

public class TestEvent {
    private TestEventDetails eventDetails;
    private TestEventSchedule eventSchedule;
    private String organizer;

    public TestEvent() {

    }

    public TestEvent(TestEventDetails eventDetails, TestEventSchedule eventSchedule, String organizer) {
        this.eventDetails = eventDetails;
        this.eventSchedule = eventSchedule;
        this.organizer = organizer;
    }

    public TestEventDetails getEventDetails() {
        return eventDetails;
    }

    public TestEventSchedule getEventSchedule() {
        return eventSchedule;
    }

    public String getOrganizer() {
        return organizer;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestEvent testEvent = (TestEvent) o;
        return Objects.equals(eventDetails, testEvent.eventDetails) && Objects.equals(eventSchedule, testEvent.eventSchedule) && Objects.equals(organizer, testEvent.organizer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventDetails, eventSchedule, organizer);
    }
}
