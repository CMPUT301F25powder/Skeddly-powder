package com.example.skeddly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.ProfileFragmentBinding;

public class CustomActivity extends AppCompatActivity {
    private ViewBinding binding;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
