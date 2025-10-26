package com.example.skeddly.business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User extends DatabaseObject {
    private boolean admin;

    private ArrayList<Event> ownedEvents;
    public ArrayList<Event> joinedEvents;

    @SuppressLint("HardwareIds")
    public User() {
        this.ownedEvents = new ArrayList<Event>();

//        this.isAdmin = false; // Enforced in realtime db rules.
    }

    public ArrayList<Event> getOwnedEvents() {
        return ownedEvents;
    }

    public void setOwnedEvents(ArrayList<Event> ownedEvents) {
        this.ownedEvents = ownedEvents;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
