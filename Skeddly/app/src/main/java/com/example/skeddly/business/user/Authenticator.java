package com.example.skeddly.business.user;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.example.skeddly.business.database.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

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

        Task<String> fcmTokenTask = FirebaseMessaging.getInstance().getToken();
        Task<DocumentSnapshot> userFetchTask = this.databaseHandler.getUsersPath().document(currentUser.getUid()).get();

        // Wait for user fetch and fcm token retrieval to finsih before continuing
        Tasks.whenAll(fcmTokenTask, userFetchTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = userFetchTask.getResult();
                DocumentReference userPath = databaseHandler.getUsersPath().document(currentUser.getUid());

                // Create a blank user if there is no user in DB
                // Use DB user if there is a user in DB
                if (!documentSnapshot.exists()) {
                    user = new User();
                    user.setId(currentUser.getUid());
                    user.setFcmToken(fcmTokenTask.getResult());
                } else {
                    user = documentSnapshot.toObject(User.class);

                    // Update FCM Token if it changed
                    String curFcmToken = fcmTokenTask.getResult();
                    if (user.getFcmToken() == null || !user.getFcmToken().equals(curFcmToken)) {
                        user.setFcmToken(curFcmToken);
                        userPath.update("fcmToken", curFcmToken);
                    }

                    try {
                        databaseHandler.customUnserializer(userPath, user);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (!user.getPersonalInformation().isFullyFilled()) {
                    showSignUp = true;
                }

                if (callback != null) {
                    callback.onUserLoaded(user, isShowSignUp());
                }
            } else {
                throw new RuntimeException(task.getException());
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
        databaseHandler.getUsersPath().document(user.getId()).delete();
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
     * @return True if the signup page needs to be shown. False otherwise.
     */
    public boolean isShowSignUp() {
        return showSignUp;
    }

    /**
     * Commits the user changes to the database
     * @see User
     */
    public void commitUserChanges() {
        DocumentReference userPath = databaseHandler.getUsersPath().document(user.getId());

        userPath.set(user);
        databaseHandler.customSerializer(userPath, user);
    }
}
