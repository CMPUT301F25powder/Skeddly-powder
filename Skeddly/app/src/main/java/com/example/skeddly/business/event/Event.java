package com.example.skeddly.business.event;

import com.example.skeddly.business.ParticipantList;
import com.example.skeddly.business.WaitingList;
import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.location.CustomLocation;
import com.google.android.gms.maps.model.LatLng;

public class Event extends DatabaseObject {
    private EventDetail eventDetails;
    private EventSchedule eventSchedule;
    private CustomLocation location;
    private String organizer;
    private WaitingList waitingList;
    private ParticipantList participantList;

    public Event() {

    }

    public Event(EventDetail eventDetails, EventSchedule eventSchedule, LatLng location,
                 String organizer, int waitingListLimit, int participantListLimit) {
        this.eventDetails = eventDetails;
        this.eventSchedule = eventSchedule;
        this.location = new CustomLocation(location.longitude, location.latitude);
        this.organizer = organizer;
        this.waitingList = new WaitingList(waitingListLimit);
        this.participantList = new ParticipantList(participantListLimit);
    }

    public Event(EventDetail eventDetails, EventSchedule eventSchedule, LatLng location,
                 String organizer, int participantListLimit) {
        this(eventDetails, eventSchedule, location, organizer, 0, participantListLimit);
    }

    public EventDetail getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(EventDetail eventDetails) {
        this.eventDetails = eventDetails;
    }

    public EventSchedule getEventSchedule() {
        return eventSchedule;
    }

    public void setEventSchedule(EventSchedule eventSchedule) {
        this.eventSchedule = eventSchedule;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public void setLocation(CustomLocation location) {
        this.location = location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public WaitingList getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(WaitingList waitingList) {
        this.waitingList = waitingList;
    }

    public ParticipantList getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ParticipantList participantList) {
        this.participantList = participantList;
    }
}
