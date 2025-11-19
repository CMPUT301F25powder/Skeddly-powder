package com.example.skeddly.business.dbnew;

import java.util.Objects;

public class TestPersonalInformation {
    private String name;
    private String email;
    private String phoneNumber;

    public TestPersonalInformation() {

    }

    public TestPersonalInformation(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestPersonalInformation that = (TestPersonalInformation) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, phoneNumber);
    }
}
