package com.example.skeddly.business.event;

import androidx.annotation.NonNull;

import com.example.skeddly.business.ParticipantList;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.WaitingList;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.location.CustomLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * An event that can be serialized into the DB
 */
public class Event extends DatabaseObject {
    private EventDetail eventDetails;
    private EventSchedule eventSchedule;
    private CustomLocation location;
    private String organizer;
    private WaitingList waitingList;
    private ParticipantList participantList;

    private String imageb64;

    /**
     * No arg Constructor for the Event
     */
    public Event() {

    }

    /**
     * Constructor for the Event
     * @param eventDetails The details of the event
     * @param eventSchedule The schedule of the event
     * @param location The location of the event
     * @param organizer The organizer of the event
     * @param waitingListLimit The limit of the waiting list
     * @param participantListLimit The limit of the participant list
     * @param image The image of the event as base64
     */
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

    /**
     * Constructor for the Event without bounds on waiting list
     * @param eventDetails The details of the event
     * @param eventSchedule The schedule of the event
     * @param location The location of the event
     * @param organizer The organizer of the event
     * @param participantListLimit The limit of the participant list
     * @param image The image of the event as base64
     */
    public Event(EventDetail eventDetails, EventSchedule eventSchedule, LatLng location,
                 String organizer, int participantListLimit, byte[] image) {
        this(eventDetails, eventSchedule, location, organizer, 0, participantListLimit, image);
    }

    /**
     * Gets details of the events
     * @return The details of the events
     */
    public EventDetail getEventDetails() {
        return eventDetails;
    }

    /**
     * Sets the details of the events
     * @param eventDetails The details of the events
     */
    public void setEventDetails(EventDetail eventDetails) {
        this.eventDetails = eventDetails;
    }

    /**
     * Gets the schedule of the event
     * @return The schedule of the event
     */
    public EventSchedule getEventSchedule() {
        return eventSchedule;
    }

    /**
     * Sets the schedule of the event
     * @param eventSchedule The schedule of the event
     */
    public void setEventSchedule(EventSchedule eventSchedule) {
        this.eventSchedule = eventSchedule;
    }

    /**
     * Gets the location of the event
     * @return The location of the event
     */
    public CustomLocation getLocation() {
        return location;
    }

    /**
     * Sets the location of the event
     * @param location The location of the event
     */
    public void setLocation(CustomLocation location) {
        this.location = location;
    }

    /**
     * Gets the organizer of the event
     * @return The organizer of the event
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * Sets the organizer of the event
     * @param organizer The organizer of the event
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     * Gets the waiting list of the event
     * @return The waiting list of the event
     */

    public WaitingList getWaitingList() {
        return waitingList;
    }

    /**
     * Sets the waiting list of the event
     * @param waitingList The waiting list of the event
     */
    public void setWaitingList(WaitingList waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * Gets the participant list of the event
     * @return The participant list of the event
     */
    public ParticipantList getParticipantList() {
        return participantList;
    }

    /**
     * Sets the participant list of the event
     * @param participantList The participant list of the event
     */
    public void setParticipantList(ParticipantList participantList) {
        this.participantList = participantList;
    }

    /**
     * Gets the image of the event
     * @return The image of the event
     */
    public String getImageb64() {
        return imageb64;
    }

    /**
     * Sets the image of the event
     * @param imageb64 The image of the event as base64
     */
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

    /**
     * Finds the ticket ID of a user in the event's waiting list.
     * @param userId The ID of the user to search for.
     * @param dbHandler The database handler to interact with Firebase.
     * @param callback The callback to be invoked when the search is complete.
     */
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
}
