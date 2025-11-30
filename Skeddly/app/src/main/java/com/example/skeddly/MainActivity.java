package com.example.skeddly;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.UserLoaded;
import com.example.skeddly.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

/**
 * Main activity for the application.
 */
public class MainActivity extends AppCompatActivity {
    private Authenticator authenticator;
    private ActivityMainBinding binding;
    private NavController navController;
    private Uri qr;
    private boolean navToInbox;

    // FCM (Notifications)
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(this, "Notification permission denied. You will not see notifications.", Toast.LENGTH_SHORT);
                }
            });

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
        qr = null;

        if (extras != null) {
            qr = Objects.requireNonNull(extras).getParcelable("QR");
            navToInbox = extras.getBoolean("notification");
        }

        DatabaseHandler database = new DatabaseHandler();
        authenticator = new Authenticator(this, database);
        authenticator.addListenerForUserLoaded(new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser, boolean shouldShowSignupPage) {
                // Update navbar if user object changes (allows for realtime updates)
                setupNavBar();

                if (qr != null) {
                    String eventId = qr.getEncodedPath();

                    if (eventId != null && eventId.length() > 1) {
                        String choppedEventId = eventId.substring(1);
                        EventRepository eventRepository = new EventRepository(FirebaseFirestore.getInstance());

                        eventRepository.get(choppedEventId).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Event does not exist!", Toast.LENGTH_SHORT).show();
                            } else {
                                navigateToEvent(task.getResult());
                            }
                        });
                    }
                } else if (navToInbox) {
                    navController.navigate(R.id.navigation_inbox);
                }
            }
        });

        setupGooglePlayServices();
        createNotificationChannel();
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
        bundle.putString("organizerId", event.getOrganizer());

        navController.navigate(R.id.navigation_event_view_info, bundle);
    }

    private void setupGooglePlayServices() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        askNotificationPermission();
                    }
                }
            });
        } else {
            askNotificationPermission();
        }
    }

    /**
     * Asks the user for notification permissions if needed
     */
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = SkeddlyFirebaseMessagingService.CHANNEL_NAME;
            String description = SkeddlyFirebaseMessagingService.CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(SkeddlyFirebaseMessagingService.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
