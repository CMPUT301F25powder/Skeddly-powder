package com.example.skeddly.business;

import android.location.Location;

import androidx.annotation.NonNull;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

    public void setApplicants(WaitingList applicants) {
        this.applicants = applicants;
    }

    public ParticipantList getAttendees() {
        return attendees;
    }

    public void setAttendees(ParticipantList attendees) {
        this.attendees = attendees;
    }

    /**
     * Handles the logic for a user joining the event's waitlist.
     * @param dbHandler The database handler to interact with Firebase.
     * @param userId The ID of the user who is joining.
     */
    public void join(DatabaseHandler dbHandler, String userId) {
        CustomLocation location = null; // Location is not implemented yet

        // Ensure applicants object and list exist to prevent NullPointerException
        if (this.getApplicants() == null) {
            this.setApplicants(new WaitingList());
        }
        if (this.getApplicants().getTicketIds() == null) {
            this.getApplicants().setTicketIds(new ArrayList<>());
        }

        // Create a new ticket for the user
        Ticket ticket = new Ticket(userId, location);

        // Add the ticket's ID to the event's applicants list
        this.getApplicants().addTicket(ticket.getId());

        // Save the updated applicants object back to this event in Firebase
        dbHandler.getEventsPath().child(this.getId()).child("applicants").setValue(this.getApplicants());

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
        if (this.getApplicants() != null && this.getApplicants().getTicketIds() != null) {
            // remove ticket id from event waiting list
            this.getApplicants().remove(ticketId);
            // save updated list to DB
            dbHandler.getEventsPath().child(this.getId()).child("applicants").setValue(this.getApplicants());
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
        if (this.getApplicants() == null || this.getApplicants().getTicketIds() == null || this.getApplicants().getTicketIds().isEmpty()) {
            callback.onResult(null); // No applicants, so user can't be joined
            return;
        }

        final boolean[] found = {false};
        ArrayList<String> ticketIds = new ArrayList<>(this.getApplicants().getTicketIds());

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
