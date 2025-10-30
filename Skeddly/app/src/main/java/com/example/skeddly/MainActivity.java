package com.example.skeddly;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.skeddly.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.Event;
import com.example.skeddly.business.database.IterableListenUpdate;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLoaded;

import java.util.ArrayList;

public class MainActivity extends CustomActivity {
    private User user;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Boiler Plate Stuff
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Inflate the layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Don't go off the screen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Dont need bottom padding since nav bar takes care of it
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        DatabaseHandler database = new DatabaseHandler(this);

        user = (User) Objects.requireNonNull(getIntent().getExtras()).getSerializable("USER");

        System.out.println(user.getExtraInformation().getName());

        setupNavBar();
    }

    private void setupNavBar() {
        // Setup the nav bar
        BottomNavigationView navView = binding.navView;

        // Clear whatever menu used to be there
        navView.getMenu().clear();

        // Get nav controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        // Inflate the correct navGraph for our privilege level and set the icons properly
        NavGraph navGraph;
        switch (user.getPrivilegeLevel()) {
            case ENTRANT:
                navView.inflateMenu(R.menu.bottom_nav_entrant);
                navGraph = navController.getNavInflater().inflate(R.navigation.entrant_navigation);
                break;
            case ORGANIZER:
                navView.inflateMenu(R.menu.bottom_nav_organizer);
                navGraph = navController.getNavInflater().inflate(R.navigation.organizer_navigation);
                break;
            case ADMIN:
                navView.inflateMenu(R.menu.botton_nav_admin);
                navGraph = navController.getNavInflater().inflate(R.navigation.admin_navigation);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + user.getPrivilegeLevel());
        }
        navController.setGraph(navGraph);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
