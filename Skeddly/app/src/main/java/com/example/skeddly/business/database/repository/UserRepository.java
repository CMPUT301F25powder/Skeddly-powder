package com.example.skeddly.business.database.repository;

import com.example.skeddly.business.user.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserRepository extends GenericRepository<User> {
    private final FirebaseFirestore firestore;
    public static final String COLLECTION_PATH = "users";

    public UserRepository(FirebaseFirestore firestore) {
        super(User.class);
        this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollectionPath() {
        return firestore.collection(COLLECTION_PATH);
    }

    @Override
    protected Query getQuery() {
        return getCollectionPath().orderBy("personalInformation.name", Query.Direction.ASCENDING);
    }
}
