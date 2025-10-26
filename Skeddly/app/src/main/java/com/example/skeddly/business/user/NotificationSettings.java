package com.example.skeddly.business.user;

public class NotificationSettings {
private boolean lotteryStatus;
private boolean eventUpdate;
private boolean administrative;

    public NotificationSettings() {
        this.lotteryStatus = false;
        this.eventUpdate = false;
        this.administrative = false;
    }

    public boolean getLotteryStatus() {
        return lotteryStatus;
    }

    public void setLotteryStatus(boolean lotteryStatus) {
        this.lotteryStatus = lotteryStatus;
    }

    public boolean getEventUpdate() {
        return eventUpdate;
    }

    public void setEventUpdate(boolean eventUpdate) {
        this.eventUpdate = eventUpdate;
    }

    public boolean getAdministrative() {
        return administrative;
    }

    public void setAdministrative(boolean administrative) {
        this.administrative = administrative;
    }
}
