package com.example.skeddly.business.user;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.skeddly.business.database.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.InvocationTargetException;

/**
 * Handles the authentication of a user. Singleton
 */
public class Authenticator {
    private static Authenticator instance;
    private User user;
    private final FirebaseAuth mAuth;
    private final DatabaseHandler databaseHandler;

    /**
     * Constructor for the Authenticator
     */
    protected Authenticator() {
        this.databaseHandler = DatabaseHandler.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public static Authenticator getInstance() {
        if (instance == null) {
            instance = new Authenticator();
        }

        return instance;
    }

    /**
     * Creates a user object based on what is in the DB and waits for it to load
     */
    private void createAndTieUser(UserLoaded callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            throw new RuntimeException();
        }

        databaseHandler.getUsersPath().document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    DocumentReference userPath = databaseHandler.getUsersPath().document(currentUser.getUid());

                    if (!result.exists()) {
                        user = new User(currentUser.getUid());
                        userPath.set(user);
                    } else {
                        user = result.toObject(User.class);

                        try {
                            databaseHandler.customUnserializer(userPath, user);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (callback != null) {
                        callback.onUserLoaded(Authenticator.this);
                    }
                } else {
                    throw new RuntimeException();
                }
            }
        });
    }

    public void signIn(UserLoaded callback) {
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                throw new RuntimeException();
            } else {
                createAndTieUser(callback);
            }
        });
    }

    /**
     * Deletes the user from the database
     * @see User
     */
    public Task<Void> deleteUser() {
        Task<Void> dbDelTask = databaseHandler.getUsersPath().document(user.getId()).delete();
        Task<Void> deleteAuthTask = mAuth.getCurrentUser().delete();

        user = null;

        return Tasks.whenAll(dbDelTask, deleteAuthTask);
    }

    /**
     * Gets the initialized user
     * @return User
     */
    public User getUser() {
        return user;
    }

    /**
     * Commits the user changes to the database
     * @see User
     */
    public void commitUserChanges() {
        databaseHandler.getUsersPath().document(user.getId()).set(user);
        databaseHandler.customSerializer(databaseHandler.getUsersPath().document(user.getId()), user);
    }
}
