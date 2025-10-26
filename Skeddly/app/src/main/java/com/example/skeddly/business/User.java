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
    private String androidId;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private Context context;

    private boolean admin;

    private ArrayList<Event> ownedEvents;
    public ArrayList<Event> joinedEvents;

    @SuppressLint("HardwareIds")
    public User(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        this.androidId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        this.ownedEvents = new ArrayList<Event>();

        this.authenticate();

//        this.isAdmin = false; // Enforced in realtime db rules.
    }

    public User() {}

    private void authenticate() {
        String emailUUID = String.valueOf(UUID.nameUUIDFromBytes(androidId.getBytes()));
        String emailGen = emailUUID + "@skeddly.com";

        mAuth.createUserWithEmailAndPassword(emailGen, androidId);

        mAuth.signInWithEmailAndPassword(emailGen, androidId).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        this.fUser = mAuth.getCurrentUser();

        if (this.fUser != null) {
            this.setId(this.fUser.getUid());
        }
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
