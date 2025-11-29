package com.example.skeddly;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class SkeddlyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
