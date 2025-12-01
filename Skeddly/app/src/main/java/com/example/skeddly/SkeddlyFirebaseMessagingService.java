package com.example.skeddly;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Messaging service for managing foreground notifications.
 */
public class SkeddlyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String CHANNEL_ID = "SKEDDLY_ALL";
    public static final String CHANNEL_NAME = "All";
    public static final String CHANNEL_DESCRIPTION = "All Skeddly Notifications";
    private static int notif_id = 0;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // Waste of time if notification not granted :(
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                        int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // Get notification
        RemoteMessage.Notification notification = message.getNotification();

        if (notification == null) {
            return;
        }

        String title = notification.getTitle();
        String body = notification.getBody();

        // Launch app when click and tell it to go to inbox
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("google.message_id", String.valueOf(notif_id));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(notif_id, builder.build());
        ++notif_id;
    }
}
