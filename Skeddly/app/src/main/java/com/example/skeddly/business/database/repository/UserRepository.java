package com.example.skeddly.business.database.repository;

import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A class to handle retrieving users from Firestore.
 */
public class UserRepository extends GenericRepository<User> {
    private final FirebaseFirestore firestore;
    private final UserLevel level;
    public static final String COLLECTION_PATH = "users";

    /**
     * Create a new UserRepository.
     * @param firestore The FirebaseFirestore instance to use.
     */
    public UserRepository(FirebaseFirestore firestore) {
        super(User.class);
        this.firestore = firestore;
        this.level = null;
    }

    /**
     * Create a new UserRepository that filters by a specific user privilege level.
     * @param firestore The FirebaseFirestore instance to use.
     * @param level The privilege level to filter by.
     */
    public UserRepository(FirebaseFirestore firestore, UserLevel level) {
        super(User.class);
        this.firestore = firestore;
        this.level = level;
    }

    @Override
    protected CollectionReference getCollectionPath() {
        return firestore.collection(COLLECTION_PATH);
    }

    @Override
    protected Query getQuery() {
        // Get users of a level
        Query query = super.getQuery();

        if (level != null) {
            query = query.whereEqualTo("privilegeLevel", level);
        }

        return query.orderBy("personalInformation.name", Query.Direction.ASCENDING);
    }
}
