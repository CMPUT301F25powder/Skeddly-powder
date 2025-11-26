package com.example.skeddly.business.database.repository;

import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserRepository extends GenericRepository<User> {
    private final FirebaseFirestore firestore;
    private final UserLevel level;
    public static final String COLLECTION_PATH = "users";

    public UserRepository(FirebaseFirestore firestore) {
        super(User.class);
        this.firestore = firestore;
        this.level = null;
    }

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
