package com.example.skeddly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.filters.LargeTest;

import com.example.skeddly.business.dbnew.DatabaseHandlerNew;
import com.example.skeddly.business.dbnew.TestEvent;
import com.example.skeddly.business.dbnew.TestEventDetails;
import com.example.skeddly.business.dbnew.TestEventSchedule;
import com.example.skeddly.business.dbnew.TestPersonalInformation;
import com.example.skeddly.business.dbnew.TestUser;
import com.example.skeddly.business.dbnew.TestNotificationSettings;
import com.example.skeddly.business.user.UserLevel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@LargeTest
public class DatabaseHandlerNewTest {
    private DatabaseHandlerNew dbHandler;
    private TestUser testUser;
    private static final String UID = "jacky";

    @Before
    public void setup() {
        dbHandler = new DatabaseHandlerNew();
        testUser = new TestUser(new TestPersonalInformation("Jacky", "jacky@jacky.com", "780-604-4973"), new TestNotificationSettings(true, false), UserLevel.ADMIN);
    }

    @Test
    public void testSetUser() throws ExecutionException, InterruptedException {
        Task<Void> addUserTask = dbHandler.addUser(testUser, UID);
        Tasks.await(addUserTask);
        assertTrue(addUserTask.isSuccessful());
    }

    @Test
    public void testRetrieveUser() throws ExecutionException, InterruptedException {
        Task<Void> addUserTask = dbHandler.addUser(testUser, UID);
        Tasks.await(addUserTask);
        assertTrue(addUserTask.isSuccessful());

        Task<DocumentSnapshot> getUserTask = dbHandler.getUser(UID);
        Tasks.await(getUserTask);
        assertTrue(getUserTask.isSuccessful());

        TestUser retrievedUser = getUserTask.getResult().toObject(TestUser.class);
        assertNotNull(retrievedUser);
        assertEquals(testUser, retrievedUser);
    }

    @Test
    public void testEventQuery() throws ExecutionException, InterruptedException {
        Task<Void> addUserTask = dbHandler.addUser(testUser, UID);
        Tasks.await(addUserTask);
        assertTrue(addUserTask.isSuccessful());

        ArrayList<TestEvent> events = new ArrayList<>();
        ArrayList<Task<DocumentReference>> tasks = new ArrayList<>();

        // Add 100 events to the db
        for (int i = 0; i < 100; ++i) {
            TestEventDetails details = new TestEventDetails(Integer.toString(i), Integer.toString(i));
            TestEventSchedule schedule = new TestEventSchedule(i, i);

            String organizer = i % 2 == 0 ? UID : String.format("not%s", UID);

            events.add(new TestEvent(details, schedule, organizer));

            tasks.add(dbHandler.addEvent(events.getLast()));
        }

        Tasks.await(Tasks.whenAllComplete(tasks));

        Task<QuerySnapshot> queryTask = dbHandler.getEventByOrganizer(UID);
        Tasks.await(queryTask);

        QuerySnapshot queryResults = queryTask.getResult();

        int i = 0;
        for (QueryDocumentSnapshot document : queryResults) {
            TestEvent event = document.toObject(TestEvent.class);

            assertEquals(UID, event.getOrganizer());
            ++i;

        }
        assertEquals(50, i);


        Task<QuerySnapshot> allEventsQueryTask = dbHandler.getEvents();
        Tasks.await(allEventsQueryTask);

        QuerySnapshot allEventsResult = allEventsQueryTask.getResult();
        ArrayList<Task<Void>> deleteTasks = new ArrayList<>();
        for (QueryDocumentSnapshot document : allEventsResult) {
            deleteTasks.add(document.getReference().delete());
        }

        Tasks.await(Tasks.whenAllComplete(deleteTasks));
    }
}
