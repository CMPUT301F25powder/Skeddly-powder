package com.example.skeddly.business;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseHandler {
    private User user;
    private Context context;
    private DatabaseReference database;

    public DatabaseHandler(Context context) {
        this.context = context;
        this.user = new User(context);
        this.database = FirebaseDatabase.getInstance().getReference();

        database.child("users").child(user.getId()).setValue(user);

        listeners();
    }

    private void listeners() {
        // Listen for any changes to user events
        iterableListen(database.child("events"), Event.class, new IterableListenUpdate<Event>() {
            @Override
            public void onUpdate(ArrayList<Event> newValues) {
                user.setOwnedEvents(newValues);
            }
        });

        // Listen for any changes to the user itself
        singleListen(database.child("users").child(user.getId()), User.class, new SingleListenUpdate<User>() {
            @Override
            public void onUpdate(User newValue) {
                user = newValue;

                System.out.println(newValue.isAdmin());
            }
        });
    }

    private <T extends DatabaseObject> void singleListen(DatabaseReference ref, Class<T> classType, SingleListenUpdate callback) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                T value = snapshot.getValue(classType);

                if (value != null) {
                    callback.onUpdate(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }
    private <T extends DatabaseObject> void iterableListen(DatabaseReference ref, Class<T> classType, IterableListenUpdate callback) {
        ref.addValueEventListener(new ValueEventListener() {
            ArrayList<T> result = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> subSnapshot = snapshot.getChildren();

                for (DataSnapshot item : subSnapshot) {
                    T value = item.getValue(classType);
                    value.setId(item.getKey());

                    result.add(value);
                }

                callback.onUpdate(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }
}
