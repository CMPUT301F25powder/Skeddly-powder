package com.example.skeddly;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skeddly.business.Authenticator;
import com.example.skeddly.business.DatabaseHandler;
import com.example.skeddly.business.Event;
import com.example.skeddly.business.IterableListenUpdate;
import com.example.skeddly.business.SingleListenUpdate;
import com.example.skeddly.business.User;
import com.example.skeddly.business.UserLoaded;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseHandler database = new DatabaseHandler(this);
        Authenticator authenticator = new Authenticator(this, database, new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser) {
                user = loadedUser;

                // Listen for any changes to events
                database.iterableListen(database.getEventsPath(), Event.class, new IterableListenUpdate<Event>() {
                    @Override
                    public void onUpdate(ArrayList<Event> newValues) {
                        user.setOwnedEvents(newValues);
                    }
                });

                // Listen for any changes to the user itself
                database.singleListen(database.getUsersPath().child(user.getId()), User.class, new SingleListenUpdate<User>() {
                    @Override
                    public void onUpdate(User newValue) {
                        user = newValue;

                        System.out.println(user.isAdmin());
                    }
                });

                System.out.println("USER");
                System.out.println(user.isAdmin());
            }
        });
    }
}