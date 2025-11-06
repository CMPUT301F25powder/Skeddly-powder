package com.example.skeddly;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLoaded;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class UserInstrumentedTest {
    /**
     * Test if a user successfully logs in
     */
    @Test
    public void testLoginUser() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        DatabaseHandler database = new DatabaseHandler();
        Authenticator authenticator = new Authenticator(appContext, database);
        authenticator.addListenerForUserLoaded(new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser, boolean shouldShowSignup) {
                assertNotNull(loadedUser);
            }
        });
    }

    /**
     * Tests if a user can access the events that it owns
     */
    // FOR NOW: create an event beforehand in the DB.
    // This will need to be fixed later once more logic is added for adding/removing events.
    // I used a user that is the owner of a test event for this test.
    @Test
    public void testEventOwnership() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        DatabaseHandler database = new DatabaseHandler();
        Authenticator authenticator = new Authenticator(appContext, database);
        authenticator.addListenerForUserLoaded(new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser, boolean shouldShowSignup) {
                assertEquals(1, loadedUser.getOwnedEvents().size());
            }
        });
    }

    /**
     * Tests if the user can edit/create a different account in the database that isn't related to them.
     * Used for testing the rules in the Firebase DB.
     */
    @Test
    public void testUserEditSecurity() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        DatabaseHandler database = new DatabaseHandler();
        Authenticator authenticator = new Authenticator(appContext, database);
        authenticator.addListenerForUserLoaded(new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser, boolean shouldShowSignup) {
                User fakeUser = new User();

                assertFalse(database.getUsersPath().child(String.valueOf(UUID.randomUUID())).setValue(fakeUser).isSuccessful());
            }
        });
    }

    @Test
    public void testDeleteUser() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        DatabaseHandler database = new DatabaseHandler();
        Authenticator authenticator = new Authenticator(appContext, database);
        authenticator.addListenerForUserLoaded(new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser, boolean shouldShowSignup) {
                authenticator.deleteUser();
            }
        });
    }
}
