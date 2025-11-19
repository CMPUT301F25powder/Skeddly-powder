package com.example.skeddly.business.dbnew;

import com.example.skeddly.business.user.UserLevel;

import java.util.Objects;

public class TestUser {
    private TestPersonalInformation personalInformation;
    private TestNotificationSettings notificationSettings;
    private UserLevel privilegeLevel;

    public TestUser() {

    }

    public TestUser(TestPersonalInformation personalInformation, TestNotificationSettings notificationSettings, UserLevel privilegeLevel) {
        this.personalInformation = personalInformation;
        this.notificationSettings = notificationSettings;
        this.privilegeLevel = privilegeLevel;
    }

    public TestPersonalInformation getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(TestPersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    public TestNotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(TestNotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public UserLevel getPrivilegeLevel() {
        return privilegeLevel;
    }

    public void setPrivilegeLevel(UserLevel privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestUser testUser = (TestUser) o;
        return Objects.equals(personalInformation, testUser.personalInformation) && Objects.equals(notificationSettings, testUser.notificationSettings) && privilegeLevel == testUser.privilegeLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalInformation, notificationSettings, privilegeLevel);
    }
}
