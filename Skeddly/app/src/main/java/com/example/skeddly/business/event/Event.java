package com.example.skeddly.business.event;

import androidx.annotation.NonNull;

import com.example.skeddly.business.ParticipantList;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.WaitingList;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.location.CustomLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.skeddly.business.Notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Event extends DatabaseObject {
    private EventDetail eventDetails;
    private EventSchedule eventSchedule;
    private CustomLocation location;
    private String organizer;
    private WaitingList waitingList;
    private ParticipantList participantList;

    private String imageb64;

    public Event() {

    }

    public Event(EventDetail eventDetails, EventSchedule eventSchedule, LatLng location,
                 String organizer, int waitingListLimit, int participantListLimit, byte[] image) {
        this.eventDetails = eventDetails;
        this.eventSchedule = eventSchedule;
        this.location = new CustomLocation(location.longitude, location.latitude);
        this.organizer = organizer;
        this.waitingList = new WaitingList(waitingListLimit);
        this.participantList = new ParticipantList(participantListLimit);

        this.imageb64 = Base64.getEncoder().encodeToString(image);
    }

    public Event(EventDetail eventDetails, EventSchedule eventSchedule, LatLng location,
                 String organizer, int participantListLimit, byte[] image) {
        this(eventDetails, eventSchedule, location, organizer, 0, participantListLimit, image);
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

    public String getImageb64() {
        return imageb64;
    }

    public void setImageb64(String imageb64) {
        this.imageb64 = imageb64;
    }

    /**
     * Handles the logic for a user joining the event's waitlist.
     * @param dbHandler The database handler to interact with Firebase.
     * @param userId The ID of the user who is joining.
     */
    public void join(DatabaseHandler dbHandler, String userId) {
        CustomLocation location = null; // Location is not implemented yet

        // Ensure applicants object and list exist to prevent NullPointerException
        if (this.getWaitingList() == null) {
            this.setWaitingList(new WaitingList());
        }
        if (this.getWaitingList().getTicketIds() == null) {
            this.getWaitingList().setTicketIds(new ArrayList<>());
        }

        // Create a new ticket for the user
        Ticket ticket = new Ticket(userId, location);

        // Add the ticket's ID to the event's applicants list
        this.getWaitingList().addTicket(ticket.getId());

        // Save the updated applicants object back to this event in Firebase
        dbHandler.getEventsPath().child(this.getId()).child("waitingList").setValue(this.getWaitingList());

        // Save the full ticket object
        dbHandler.getTicketsPath().child(ticket.getId()).setValue(ticket);
    }

    /**
     * Handles the logic for a user leaving the event's waitlist.
     * @param dbHandler The database handler to interact with Firebase.
     * @param ticketId The ID of the ticket to be removed.
     */
    public void leave(DatabaseHandler dbHandler, String ticketId) {
        // Ensure applicants and ticket list exist
        if (this.getWaitingList() != null && this.getWaitingList().getTicketIds() != null) {
            // remove ticket id from event waiting list
            this.getWaitingList().remove(ticketId);
            // save updated list to DB
            dbHandler.getEventsPath().child(this.getId()).child("waitingList").setValue(this.getWaitingList());
            // remove ticket object from DB
            dbHandler.getTicketsPath().child(ticketId).removeValue();
        }
    }

    /**
     * A public callback interface to return the result of an asynchronous search.
     */
    public interface FindTicketCallback {
        void onResult(String ticketId);
    }

    public void findUserTicketId(String userId, DatabaseHandler dbHandler, FindTicketCallback callback) {
        if (this.getWaitingList() == null || this.getWaitingList().getTicketIds() == null || this.getWaitingList().getTicketIds().isEmpty()) {
            callback.onResult(null); // No applicants, so user can't be joined
            return;
        }

        final boolean[] found = {false};
        ArrayList<String> ticketIds = new ArrayList<>(this.getWaitingList().getTicketIds());

        for (String ticketId : ticketIds) {
            dbHandler.getTicketsPath().child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (found[0]) return; // Stop if we already found the ticket

                    Ticket ticket = snapshot.getValue(Ticket.class);
                    if (ticket != null && userId.equals(ticket.getUser())) {
                        found[0] = true;
                        callback.onResult(snapshot.getKey()); // Found it!
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException();
                }
            });
        }
        callback.onResult(null); // Didn't find it
    }

    public void notifyWaiting(Notification notif, DatabaseHandler dbHandler) {
        SingleListenUpdate<Ticket> getUserFromTicket = (ticket) -> {
           String userId = ticket.getUser();
        };
        for (String ticketId : this.getWaitingList().getTicketIds()) {
            dbHandler.singleListen(dbHandler.getTicketsPath().child(ticketId), Ticket.class, getUserFromTicket);
        }
    }

}
