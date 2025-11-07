package com.example.skeddly.business.user;

import java.io.Serializable;

/**
 * A user's notification settings that can be serialized into the DB
 */
public class NotificationSettings implements Serializable {
private boolean lotteryStatus;
private boolean eventUpdate;
private boolean administrative;

    /**
     * No arg Constructor for the NotificationSettings
     */
    public NotificationSettings() {
        this.lotteryStatus = false;
        this.eventUpdate = false;
        this.administrative = false;
    }

    /**
     * Gets the lottery status
     * @return The lottery status
     */
    public boolean getLotteryStatus() {
        return lotteryStatus;
    }

    /**
     * Sets the lottery status
     * @param lotteryStatus The lottery status
     */
    public void setLotteryStatus(boolean lotteryStatus) {
        this.lotteryStatus = lotteryStatus;
    }

    /**
     * Gets the event update
     * @return The event update
     */
    public boolean getEventUpdate() {
        return eventUpdate;
    }

    /**
     * Sets the event update
     * @param eventUpdate The event update
     */
    public void setEventUpdate(boolean eventUpdate) {
        this.eventUpdate = eventUpdate;
    }

    /**
     * Gets if the user is an administrator
     * @return The administrative flag
     */
    public boolean getAdministrative() {
        return administrative;
    }

    /**
     * Sets if the user is an administrator
     * @param administrative The administrative flag
     */
    public void setAdministrative(boolean administrative) {
        this.administrative = administrative;
    }
}
