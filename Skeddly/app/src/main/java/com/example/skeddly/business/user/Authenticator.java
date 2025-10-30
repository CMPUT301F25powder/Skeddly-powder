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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Authenticator {
    private String androidId;
    private User user;
    private FirebaseAuth mAuth;
    private Context context;
    private DatabaseHandler databaseHandler;
    private boolean showSignUp;
    UserLoaded callback;

    public Authenticator(Context context, DatabaseHandler databaseHandler) {
        this.context = context;
        this.databaseHandler = databaseHandler;
        this.mAuth = FirebaseAuth.getInstance();
        this.androidId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        this.showSignUp = true;

        String emailUUID = String.valueOf(UUID.nameUUIDFromBytes(androidId.getBytes()));
        String emailGen = emailUUID + "@skeddly.com";

        mAuth.signInWithEmailAndPassword(emailGen, androidId).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    showSignUp = true;
                    // Try to sign up user - associated with device ID
                    mAuth.createUserWithEmailAndPassword(emailGen, androidId).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign up fails, usually because there is already a user made, then sign in
                            if (task.isSuccessful()) {
                                createAndTieUser();
                            }
                        }
                    });
                } else {
                    showSignUp = false;
                    createAndTieUser();
                }
            }
        });
    }

    /**
     * Creates a user object based on what is in the DB and waits for it to load
     */
    private void createAndTieUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        this.databaseHandler.getUsersPath().child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Create a blank user if there is no user in DB
                // Use DB user if there is a user in DB
                if (!dataSnapshot.exists()) {
                    user = new User();

                    DatabaseReference currentUserPath = databaseHandler.getUsersPath().child(currentUser.getUid());

                    currentUserPath.setValue(user);
                } else {
                    user = dataSnapshot.getValue(User.class);
                }

                user.setId(currentUser.getUid());

                callback.onUserLoaded(user, isShowSignUp());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    public void addListenerForUserLoaded(UserLoaded callback) {
        this.callback = callback;
    }

    public void deleteUser() {
        databaseHandler.getUsersPath().child(user.getId()).removeValue();
        mAuth.getCurrentUser().delete();
    }

    /**
     * Gets the initialized user
     * @return User
     */
    public User getUser() {
        return this.user;
    }

    public boolean isShowSignUp() {
        return showSignUp;
    }

    public void commitUserChanges() {
        databaseHandler.getUsersPath().child(user.getId()).setValue(user);
    }
}
