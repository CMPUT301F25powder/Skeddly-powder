package com.example.skeddly;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skeddly.business.user.User;

public class CustomActivity extends AppCompatActivity {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
