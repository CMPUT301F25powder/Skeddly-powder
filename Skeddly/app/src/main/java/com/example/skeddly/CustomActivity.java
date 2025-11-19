package com.example.skeddly;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skeddly.business.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;

/**
 * Custom activity class for the application.
 */
public class CustomActivity extends AppCompatActivity {
    private User user;

    protected void setupFirebaseEmulator() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.useEmulator("10.0.2.2", 9099);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.useEmulator("10.0.2.2", 8080);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .build();
        firestore.setFirestoreSettings(settings);

        FirebaseDatabase rtdb = FirebaseDatabase.getInstance();
        rtdb.useEmulator("10.0.2.2", 9000);
        rtdb.setPersistenceEnabled(false);
    }

    /**
     * Getter for the user object.
     * @return The user object.
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter for the user object.
     * @param user The user object to set.
     */
    public void setUser(User user) {
        this.user = user;
    }
}
