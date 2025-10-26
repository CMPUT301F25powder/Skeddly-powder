package com.example.skeddly.business.user;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.example.skeddly.business.database.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.UUID;

public class Authenticator {
    private String androidId;
    private User user;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private Context context;
    private DatabaseHandler databaseHandler;
    public Authenticator(Context context, DatabaseHandler databaseHandler, UserLoaded callback) {
        this.context = context;
        this.databaseHandler = databaseHandler;
        this.mAuth = FirebaseAuth.getInstance();
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

        this.createAndTieUser(callback);
    }

    private void createAndTieUser(UserLoaded callback) {
        this.databaseHandler.getUsersPath().child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("EXISTS");
                System.out.println(dataSnapshot.exists());
                if(!dataSnapshot.exists()) {
                    user = new User();
                } else {
                    user = dataSnapshot.getValue(User.class);
                }

                user.setId(fUser.getUid());

                callback.onUserLoaded(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    public User getUser() {
        return this.user;
    }
}
