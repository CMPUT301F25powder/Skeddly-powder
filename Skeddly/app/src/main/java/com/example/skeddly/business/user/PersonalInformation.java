package com.example.skeddly.business.user;

import java.io.Serializable;
import java.util.Objects;

/**
 * A user's personal information that can be serialized into the DB
 */
public class PersonalInformation implements Serializable {
    private String name;
    private String email;
    private String phoneNumber;

    /**
     * Constructor for the PersonalInformation
     */
    public PersonalInformation() {
        this.name = "";
        this.email = "";
        this.phoneNumber = "";
    }

    /**
     * Gets the name of user
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email of the user
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the user
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user
     * @param phoneNumber The phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Checks if the personal information is fully filled
     * @see PersonalInformation
     * @return If the personal information is fully filled
     */
    public boolean isFullyFilled() {
        return !Objects.equals(this.name, "") && !Objects.equals(this.email, "");
    }
}
