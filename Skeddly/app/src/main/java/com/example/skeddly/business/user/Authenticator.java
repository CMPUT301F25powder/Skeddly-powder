package com.example.skeddly.business.user;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

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

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Handles the authentication of a user
 */
public class Authenticator {
    private String androidId;
    private User user;
    private FirebaseAuth mAuth;
    private DatabaseHandler databaseHandler;
    private boolean showSignUp;
    UserLoaded callback;

    /**
     * Constructor for the Authenticator
     * @param context The app context
     * @param databaseHandler The database handler
     */
    public Authenticator(Context context, DatabaseHandler databaseHandler) {
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
                    DatabaseReference userPath = databaseHandler.getUsersPath().child(currentUser.getUid());

                    user = dataSnapshot.getValue(User.class);
                    try {
                        databaseHandler.customUnserializer(userPath, user);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                user.setId(currentUser.getUid());

                if (!user.getPersonalInformation().isFullyFilled()) {
                    showSignUp = true;
                }

                if (callback != null) {
                    callback.onUserLoaded(user, isShowSignUp());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Sets the callback for when the user is loaded
     * @param callback The callback to set
     */
    public void addListenerForUserLoaded(UserLoaded callback) {
        this.callback = callback;
    }

    /**
     * Deletes the user from the database
     * @see User
     */
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

    /**
     * Gets if the user needs to sign up
     * @see User
     * @return
     */
    public boolean isShowSignUp() {
        return showSignUp;
    }

    /**
     * Commits the user changes to the database
     * @see User
     */
    public void commitUserChanges() {
        DatabaseReference userPath = databaseHandler.getUsersPath().child(user.getId());

        userPath.setValue(user);
        databaseHandler.customSerializer(userPath, user);
    }
}
