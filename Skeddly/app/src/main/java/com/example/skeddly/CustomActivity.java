package com.example.skeddly;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skeddly.business.user.User;

/**
 * Custom activity class for the application.
 */
public class CustomActivity extends AppCompatActivity {
    private User user;

    /**
     * Getter for the user object.
     * @return The user object.
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter for the user object.
     * @param user The user object to set.
     */
    public void setUser(User user) {
        this.user = user;
    }
}
