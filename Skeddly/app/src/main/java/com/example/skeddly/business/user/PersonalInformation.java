package com.example.skeddly.business.user;

import java.io.Serializable;
import java.util.Objects;

public class PersonalInformation implements Serializable {
    private String name;
    private String email;
    private String phoneNumber;

    public PersonalInformation() {
        this.name = "";
        this.email = "";
        this.phoneNumber = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isFullyFilled() {
        return !Objects.equals(this.name, "") && !Objects.equals(this.email, "");
    }
}
