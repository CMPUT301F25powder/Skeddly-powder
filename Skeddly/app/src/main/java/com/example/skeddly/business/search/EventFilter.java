package com.example.skeddly.business.search;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;

import java.util.ArrayList;
import java.util.List;

public class EventFilter {
    private String startTime;
    private String endTime;
    private boolean weekend;
    private boolean weekday;
    private ArrayList<String> selectedEventTypes = new ArrayList<>();

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
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

        if (!getSelectedEventTypes().contains(eventDetails.getCategories())) {
            return false;
        }

        return true;
    }
}
