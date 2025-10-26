package com.example.skeddly.business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

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

import java.util.Objects;
import java.util.UUID;

public class User {
    private String androidId;
    private String id;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser fUser;
    private Context context;
    public boolean isAdmin;

    @SuppressLint("HardwareIds")
    public User(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

        this.androidId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);

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
            this.id = this.fUser.getUid();
        }

        this.isAdmin = false; // Enforced in realtime db rules.

        mDatabase.child("users").child(this.id).setValue(this);

        mDatabase.child("users").child(this.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User value = snapshot.getValue(User.class);

                if (value != null) {
                    isAdmin = value.isAdmin;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }

    public User() {}
}
