package com.example.skeddly.business.user;

public class NotificationSettings {
    private Boolean lotteryStatus;
    private Boolean eventUpdate;
    private Boolean administrative;

    public NotificationSettings() {
        this.lotteryStatus = false;
        this.eventUpdate = false;
        this.administrative = false;
    }

    public Boolean getLotteryStatus() {
        return lotteryStatus;
    }

    public void setLotteryStatus(Boolean lotteryStatus) {
        this.lotteryStatus = lotteryStatus;
    }

    public Boolean getEventUpdate() {
        return eventUpdate;
    }

    public void setEventUpdate(Boolean eventUpdate) {
        this.eventUpdate = eventUpdate;
    }

    public Boolean getAdministrative() {
        return administrative;
    }

    public void setAdministrative(Boolean administrative) {
        this.administrative = administrative;
    }
}
