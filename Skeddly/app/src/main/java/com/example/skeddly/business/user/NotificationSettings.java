package com.example.skeddly.business.user;

/**
 * Represent's a user's notification settings
 */
public class NotificationSettings {
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
     * Gets whether the user has lottery status notification enabled
     * @return True if the notifications are enabled. False otherwise.
     */
    public boolean getLotteryStatus() {
        return lotteryStatus;
    }

    /**
     * Sets whether the user has lottery status notification enabled
     * @param lotteryStatus True if the notifications are enabled. False otherwise.
     */
    public void setLotteryStatus(boolean lotteryStatus) {
        this.lotteryStatus = lotteryStatus;
    }

    /**
     * Gets whether the user has event update notification enabled
     * @return True if the notifications are enabled. False otherwise.
     */
    public boolean getEventUpdate() {
        return eventUpdate;
    }

    /**
     * Sets whether the user has event update notification enabled
     * @param eventUpdate True if the notifications are enabled. False otherwise.
     */
    public void setEventUpdate(boolean eventUpdate) {
        this.eventUpdate = eventUpdate;
    }

    /**
     * Gets whether the user has administrative notification enabled
     * @return True if the notifications are enabled. False otherwise.
     */
    public boolean getAdministrative() {
        return administrative;
    }

    /**
     * Sets whether the user has administrative notification enabled
     * @param administrative True if the notifications are enabled. False otherwise.
     */
    public void setAdministrative(boolean administrative) {
        this.administrative = administrative;
    }
}
