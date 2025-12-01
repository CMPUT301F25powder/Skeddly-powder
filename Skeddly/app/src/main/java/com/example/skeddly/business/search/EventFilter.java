package com.example.skeddly.business.search;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;
import com.example.skeddly.business.user.User;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Class that represents all the filterable options for the event search filter.
 */
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

    /**
     * If the filter is ready to be used.
     * @return boolean
     */
    public boolean isFinalized() {
        return isFinalized;
    }

    /**
     * Sets if the filter is ready to be used.
     * @param finalized boolean
     */
    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }

    /**
     * The start time that has been set in the filter.
     * @return LocalTime
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Set the start time for the filter.
     * @param startTime StartTime
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * The end t ime that has been set in the filter.
     * @return LocalTime
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Set the end time for the filter.
     * @param endTime LocalTime
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Get if the filter should look for events that fall on a weekend.
     * @return boolean
     */
    public boolean isWeekend() {
        return weekend;
    }

    /**
     * Set if the filter should look for events that fall on a weekend.
     * @param weekend boolean
     */
    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    /**
     * Check if the filter looks for events that fall on weekdays.
     * @return boolean
     */
    public boolean isWeekday() {
        return weekday;
    }

    /**
     * Set if the filter should look for events that fall on weekdays.
     * @param weekday boolean
     */
    public void setWeekday(boolean weekday) {
        this.weekday = weekday;
    }

    /**
     * Get the event categories that have been checked in the checkboxes and set in the filter.
     * @return ArrayList
     */
    public ArrayList<String> getSelectedEventTypes() {
        return selectedEventTypes;
    }

    /**
     * Set the event categories that have been checked in the checkboxes.
     * @param selectedEventTypes ArrayList
     */
    public void setSelectedEventTypes(ArrayList<String> selectedEventTypes) {
        this.selectedEventTypes = selectedEventTypes;
    }

    /**
     * Checks if the event meets the filter criteria
     * @param event Event
     * @return boolean
     */
    public boolean checkFilterCriteria(Event event) {
        EventDetail eventDetails = event.getEventDetails();
        EventSchedule eventSchedule = event.getEventSchedule();
        List<Boolean> daysOfWeek = eventSchedule.getDaysOfWeek();

        if (daysOfWeek != null) {
            // Recurring
            if (!(this.isWeekend() && (daysOfWeek.get(0) == true || daysOfWeek.get(1) == true))) {
                return false;
            }

            if ((this.isWeekday() && (daysOfWeek.get(0) == false && daysOfWeek.get(1) == false))) {
                return false;
            }
        } else {
            // Single day event
            LocalDate eventDate = LocalDate.ofInstant(Instant.ofEpochSecond(eventSchedule.getStartTime()), ZoneId.systemDefault());
            DayOfWeek dayOfWeek = eventDate.getDayOfWeek();

            if ((dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) && !this.isWeekend()) {
                return false;
            } else if (!(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) && !this.isWeekday()) {
                return false;
            }
        }

        if (!containsAnyCategory(eventDetails.getCategories())) {
            return false;
        }

        if (this.startTime != null && this.endTime != null) {
            LocalTime eventStartTime = LocalTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getStartTime()), ZoneId.systemDefault());
            LocalTime eventEndTime = LocalTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getEndTime()), ZoneId.systemDefault());

            if (startTime.isAfter(eventStartTime) || endTime.isBefore(eventEndTime)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether or not the current selected event types/categories intersect with the respective event.
     * @param eventDetailCategories ArrayList<String>
     * @return boolean Whether or not the intersection holds.
     */
    private boolean containsAnyCategory(ArrayList<String> eventDetailCategories) {
        Set<String> selectedEventTypesSet = new HashSet<>(this.getSelectedEventTypes());
        Set<String> eventDetailCategoriesSet = new HashSet<>(eventDetailCategories);

        Set<String> intersection = new HashSet<>(selectedEventTypesSet);
        boolean changed = intersection.retainAll(eventDetailCategoriesSet);

        return !changed;
    }

    /**
     * Returns true if there are not filters actually in place.
     * @return boolean
     */
    public boolean isBlank() {
        return (startTime == null && endTime == null && !weekend && !weekday && selectedEventTypes.isEmpty());
    }
}
