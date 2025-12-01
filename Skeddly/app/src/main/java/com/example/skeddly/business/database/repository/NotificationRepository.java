package com.example.skeddly.business.database.repository;

import androidx.annotation.Nullable;

import com.example.skeddly.business.notification.Notification;
import com.example.skeddly.business.notification.NotificationType;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A class to handle retrieving notifications from Firestore. This class is (typically) associated
 * with a particular user, which is relevant when performing aggregate retrievals.
 */
public class NotificationRepository extends GenericRepository<Notification> {
    private final FirebaseFirestore firestore;
    @Nullable
    private final String userId;
    @Nullable
    private final NotificationType notificationType;
    public static final String COLLECTION_PATH = "notifications";

    /**
     * Create a new NotificationRepository. The repository is associated with a particular user,
     * and type of notification unless the userId or type is null. If the userId is null, the
     * repository shall retrieve notifications for all users. If the type is null, the repository
     * shall retrieve notifications of any type.
     * @param firestore The FirebaseFirestore instance to use.
     * @param userId The user id that this repository is associated with.
     * @param notificationType The notification type that this repository is associated with.
     */
    public NotificationRepository(FirebaseFirestore firestore, @Nullable String userId, @Nullable NotificationType notificationType) {
        super(Notification.class);
        this.firestore = firestore;
        this.userId = userId;
        this.notificationType = notificationType;
    }

    /**
     * Create a new NotificationRepository. The repository is associated with a particular user,
     * and type of notification unless the userId is null. If it is, the repository shall retrieve
     * notifications for all users.
     * @param firestore The FirebaseFirestore instance to use.
     * @param userId The user id that this repository is associated with.
     */
    public NotificationRepository(FirebaseFirestore firestore, @Nullable String userId) {
        this(firestore, userId, null);
    }

    /**
     * Create a new NotificationRepository that is not tied to a specific user.
     * @param firestore The FirebaseFirestore instance to use.
     */
    public NotificationRepository(FirebaseFirestore firestore) {
        this(firestore, null);
    }

    @Override
    protected CollectionReference getCollectionPath() {
        return firestore.collection(COLLECTION_PATH);
    }

    @Override
    protected Query getQuery() {
        Query query = super.getQuery();

        if (userId != null) {
            query = query.whereEqualTo("recipient", userId);
        }

        if (notificationType != null) {
            query = query.whereEqualTo("type", notificationType.toString());
        }

        return query.orderBy("timestamp", Query.Direction.DESCENDING);
    }
}
