package com.example.skeddly.business.search;

import androidx.annotation.NonNull;

import com.example.skeddly.R;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;
import com.example.skeddly.business.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EventFilter {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean weekend;
    private boolean weekday;
    private ArrayList<String> selectedEventTypes = new ArrayList<>();
    private boolean isFinalized;
    private User user;

    public EventFilter(User user) {
        this.user = user;
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    public boolean isWeekday() {
        return weekday;
    }

    public void setWeekday(boolean weekday) {
        this.weekday = weekday;
    }

    public ArrayList<String> getSelectedEventTypes() {
        return selectedEventTypes;
    }

    public void setSelectedEventTypes(ArrayList<String> selectedEventTypes) {
        this.selectedEventTypes = selectedEventTypes;
    }

    public boolean checkFilterCriteria(Event event) {
        EventDetail eventDetails = event.getEventDetails();
        EventSchedule eventSchedule = event.getEventSchedule();
        List<Boolean> daysOfWeek = eventSchedule.getDaysOfWeek();

        if (daysOfWeek != null) {
            if (!(this.isWeekend() && (daysOfWeek.get(0) == true || daysOfWeek.get(1) == true))) {
                return false;
            }

            if ((this.isWeekday() && (daysOfWeek.get(0) == false && daysOfWeek.get(1) == false))) {
                return false;
            }
        }

        if (!containsAnyCategory(eventDetails.getCategories())) {
            return false;
        }

        if (this.startTime != null & this.endTime != null) {
            LocalTime eventStartTime = LocalTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getStartTime()), ZoneId.systemDefault());
            LocalTime eventEndTime = LocalTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getEndTime()), ZoneId.systemDefault());

            if (this.startTime != eventStartTime || this.endTime != eventEndTime) {
                return false;
            }
        }

        switch (user.getPrivilegeLevel()) {
            // if entrant, don't show events that are not joinable
            case ENTRANT:
                if (!event.isJoinable()) {
                    return false;
                }
                break;
            // if organizer, don't show events that are not joinable unless they are the organizer
            case ORGANIZER:
                if (!event.isJoinable() && !Objects.equals(event.getOrganizer(), user.getId())) {
                    return false;
                }
                break;
            // Admins are allowed to see everything
        }
        return true;
    }

    private boolean containsAnyCategory(ArrayList<String> eventDetailCategories) {
        Set<String> selectedEventTypesSet = new HashSet<>(this.getSelectedEventTypes());
        Set<String> eventDetailCategoriesSet = new HashSet<>(eventDetailCategories);

        Set<String> intersection = new HashSet<>(selectedEventTypesSet);
        boolean changed = intersection.retainAll(eventDetailCategoriesSet);

        return !changed;
    }
}
