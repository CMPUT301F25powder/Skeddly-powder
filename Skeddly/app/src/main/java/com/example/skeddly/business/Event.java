package com.example.skeddly.business;

public class Event {
    private String title;
    private Location location;
    // still need dates, and cosmetic info like poster

    private User organizer;
    private final WaitingList applicants;
    private final ParticipantList attendees;

    public Event(String title, User organizer, Location location, int maxWait, int maxAttend) {
        this.title = title;
        this.organizer = organizer;
        this.location = location;
        if (maxWait < 0) {
            applicants = new WaitingList(maxWait);
        } else {
            applicants = new WaitingList();
        }

        if (maxAttend > 0) {
            attendees = new ParticipantList(maxAttend);
        } else {
            attendees = new ParticipantList();
        }
    }
    public Event(String title, User organizer, Location location) {
        this(title, organizer, location, 0, 0);
    }
    public Event(String title, User organizer, Location location, int maxAttend) {
        this(title, organizer, location, 0, maxAttend);
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void draw(int num) {
        if (num <= attendees.getMaxAttend()) {
            for (int i = 0; i < num; i++) {
                attendees.addUser(applicants.draw().getUser());
            }
        }
    }

    public void draw() {
        for (int i = 0; i < attendees.getMaxAttend(); i++) {
            attendees.addUser(applicants.draw().getUser());
        }
    }

    public void removeParticipant(User u) {
        attendees.remove(u);
    }
    public void removeApplicant(User u) {
        applicants.remove(u);
    }
}
