package com.example.skeddly.business.database.repository;

import androidx.annotation.Nullable;

import com.example.skeddly.business.notification.Notification;
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
    public static final String COLLECTION_PATH = "notifications";

    /**
     * Create a new NotificationRepository. The repository is associated with a particular user,
     * unless the provided userId is null. If it is, the repository shall retrieve all notifications
     * for any user.
     * @param firestore The FirebaseFirestore instance to use.
     * @param userId The user id that this repository is associated with.
     */
    public NotificationRepository(FirebaseFirestore firestore, @Nullable String userId) {
        super(Notification.class);
        this.firestore = firestore;
        this.userId = userId;
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
        if (userId == null) {
            return super.getQuery();
        }

        return getCollectionPath().whereEqualTo("recipient", userId);
    }
}
