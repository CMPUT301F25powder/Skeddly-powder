package com.example.skeddly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLoaded;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Main activity for the application.
 */
public class MainActivity extends CustomActivity {
    private Authenticator authenticator;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Boiler Plate Stuff
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Inflate the layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Don't go off the screen
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Don't need bottom padding since nav bar takes care of it
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        Uri qr = null;

        if (extras != null) {
            qr = Objects.requireNonNull(extras).getParcelable("QR");
        }

        DatabaseHandler database = new DatabaseHandler();
        authenticator = new Authenticator(this, database);
        authenticator.addListenerForUserLoaded(new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser, boolean shouldShowSignupPage) {
                // Update navbar if user object changes (allows for realtime updates)
                setupNavBar();
            }
        });

        if (qr != null) {
            String eventId = qr.getEncodedPath();

            if (eventId != null && eventId.length() > 1) {
                String choppedEventId = eventId.substring(1);

                database.getEventsPath().child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Event theEvent = snapshot.getValue(Event.class);

                            if (theEvent == null) {
                                return;
                            }

                            theEvent.setId(choppedEventId);
                            navigateToEvent(theEvent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });
            }
        }
    }

    /**
     * Sets up the navigation bar based on the user's privilege level.
     */
    private void setupNavBar() {
        // Setup the nav bar
        BottomNavigationView navView = binding.navView;

        // Clear whatever menu used to be there
        navView.getMenu().clear();

        // Get nav controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = Objects.requireNonNull(navHostFragment).getNavController();

        // Inflate the correct navGraph for our privilege level and set the icons properly
        NavGraph navGraph;
        switch (authenticator.getUser().getPrivilegeLevel()) {
            case ENTRANT:
                navView.inflateMenu(R.menu.bottom_nav_entrant);
                navGraph = navController.getNavInflater().inflate(R.navigation.navigation_entrant);
                break;
            case ORGANIZER:
                navView.inflateMenu(R.menu.bottom_nav_organizer);
                navGraph = navController.getNavInflater().inflate(R.navigation.navigation_organizer);
                break;
            case ADMIN:
                navView.inflateMenu(R.menu.botton_nav_admin);
                navGraph = navController.getNavInflater().inflate(R.navigation.navigation_admin);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + authenticator.getUser().getPrivilegeLevel());
        }
        navController.setGraph(navGraph);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * Getter for the user object.
     * @return The user object.
     */
    public User getUser() {
        return authenticator.getUser();
    }

    /**
     * Getter for the authenticator object.
     * @return The Authenticator of the user
     */
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * Notifies the authenticator that the user has changed.
     */
    public void notifyUserChanged() {
        authenticator.commitUserChanges();
    }

    /**
     * Switches to the signup activity.
     */
    public void switchToSignup() {
        Intent signupActivity = new Intent(getBaseContext(), SignupActivity.class);
        signupActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signupActivity);
        finish();
    }

    /**
     * Navigates to the event view. Used if the app was opened with a QR code pointing to an event.
     * @param event The event to navigate to.
     */
    private void navigateToEvent(Event event) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", event.getId());
        bundle.putString("userId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        bundle.putString("organizerId", event.getOrganizer());
        navController.navigate(R.id.navigation_event_view_info, bundle);
    }
}
