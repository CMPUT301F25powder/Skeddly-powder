package com.example.skeddly.business.event;

import androidx.annotation.NonNull;

import com.example.skeddly.business.ParticipantList;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.TicketStatus;
import com.example.skeddly.business.WaitingList;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.repository.TicketRepository;
import com.example.skeddly.business.location.CustomLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Base64;

/**
 * This class represents an event.
 */
public class Event extends DatabaseObject {
    private EventDetail eventDetails;
    private EventSchedule eventSchedule;
    private CustomLocation location;
    private String organizer;
    private WaitingList waitingList;
    private ParticipantList participantList;
    private boolean logLocation;
    private String imageb64;

    /**
     * No arg Constructor for an Event. Required by Firestore.
     */
    public Event() {

    }

    /**
     * Constructor for the Event
     * @param eventDetails The details of the event
     * @param eventSchedule The schedule of the event
     * @param location The location of the event
     * @param organizer The ID of the organizer of the event
     * @param waitingListLimit The limit of the waiting list
     * @param participantListLimit The limit of the participant list
     * @param logLocation Whether your location is required to be logged when joining the waiting list.
     * @param image The image of the event as base64
     */
    public Event(EventDetail eventDetails, EventSchedule eventSchedule, LatLng location,
                 String organizer, int waitingListLimit, int participantListLimit, boolean logLocation, byte[] image) {
        this.eventDetails = eventDetails;
        this.eventSchedule = eventSchedule;
        this.location = new CustomLocation(location.longitude, location.latitude);
        this.organizer = organizer;
        this.waitingList = new WaitingList(waitingListLimit);
        this.participantList = new ParticipantList(participantListLimit);

        this.logLocation = logLocation;
        this.imageb64 = Base64.getEncoder().encodeToString(image);
    }

    /**
     * Constructor for the Event without bounds on waiting list
     * @param eventDetails The details of the event
     * @param eventSchedule The schedule of the event
     * @param location The location of the event
     * @param organizer The organizer of the event
     * @param participantListLimit The limit of the participant list
     * @param logLocation Whether your location is required to be logged when joining the waiting list.
     * @param image The image of the event as base64
     */
    public Event(EventDetail eventDetails, EventSchedule eventSchedule, LatLng location,
                 String organizer, int participantListLimit, boolean logLocation, byte[] image) {
        this(eventDetails, eventSchedule, location, organizer, 0, participantListLimit, logLocation, image);
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
     * Gets the organizer id of the event
     * @return The organizer id of the event
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * Sets the organizer of the event by their ID
     * @param organizer The organizer id to set it to
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
     * Gets whether location logging is required to join the waiting list
     * @return True if it is required. False otherwise.
     */
    public boolean getLogLocation() {
        return logLocation;
    }

    /**
     * Sets the location logging requirement for the event.
     * @param logLocation The new boolean value for the requirement.
     */
    public void setLogLocation(boolean logLocation) {
        this.logLocation = logLocation;
    }

    /**
     * Gets the image of the event as a base64 string
     * @return The image of the event as base64
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
     * Queries whether the event is joinable or not. An event is joinable if the waiting list
     * is not full and the registration period has not ended yet.
     * @return True if the event can be joined. False otherwise.
     */
    public boolean isJoinable() {
        return !waitingList.isFull() && !eventSchedule.isRegistrationOver();
    }

    /**
     * Draws a number of participants into the Participant List.
     * @param numToDraw The number of participants to draw.
     */
    public void draw(int numToDraw) {
        TicketRepository ticketRepository = new TicketRepository(FirebaseFirestore.getInstance(), getId());
        for (int i = 0; i < numToDraw; ++i) {
            String ticketId = getWaitingList().draw();
            getParticipantList().addTicket(ticketId);

            // Change the status to INVITED
            ticketRepository.updateStatus(ticketId, TicketStatus.INVITED);
        }

        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.getEventsPath().document(this.getId()).set(this);
    }

    /**
     * Handles the logic for a user joining the event's waitlist.
     * @param dbHandler The database handler to interact with Firebase.
     * @param userId The ID of the user who is joining.
     */
    public void join(DatabaseHandler dbHandler, String userId, CustomLocation location) {
        // Ensure applicants object and list exist to prevent NullPointerException
        if (this.getWaitingList() == null) {
            this.setWaitingList(new WaitingList());
        }
        if (this.getWaitingList().getTicketIds() == null) {
            this.getWaitingList().setTicketIds(new ArrayList<>());
        }

        // Create a new ticket for the user
        Ticket ticket = new Ticket(userId, getId(), location);

        // Add the ticket's ID to the event's applicants list
        this.getWaitingList().addTicket(ticket.getId());

        // Save the updated applicants object back to this event in Firebase
        dbHandler.getEventsPath().document(this.getId()).update("waitingList", this.getWaitingList());

        // Save the full ticket object
        dbHandler.getTicketsPath().document(ticket.getId()).set(ticket);
    }

    /**
     * Handles the logic for a user leaving the event's waitlist.
     * @param dbHandler The database handler to interact with Firebase.
     * @param ticketId The ID of the ticket to be removed.
     */
    public void leave(DatabaseHandler dbHandler, String ticketId) {
        // Remove ticket id from the lists
        getWaitingList().remove(ticketId);
        getParticipantList().remove(ticketId);

        // save updated lists to DB
        dbHandler.getEventsPath().document(getId()).set(this);

        // remove ticket object from DB
        dbHandler.getTicketsPath().document(ticketId).delete();
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
        dbHandler.getTicketsPath().whereEqualTo("eventId", getId()).whereEqualTo("userId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    callback.onResult(task.getResult().getDocuments().get(0).getId());  // Found it!
                } else {
                    callback.onResult(null); // Didn't find it
                }
            }
        });
    }
}
