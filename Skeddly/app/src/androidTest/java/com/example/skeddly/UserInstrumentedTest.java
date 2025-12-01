package com.example.skeddly;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static com.example.skeddly.utilities.TestUtil.onViewLoaded;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLoaded;
import com.example.skeddly.utilities.BaseTest;

import java.util.UUID;

@LargeTest
public class UserInstrumentedTest extends BaseTest {
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
     * Tests if the {@link User} is properly deleted.
     */
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

    /**
     * Tests if the user page appears when clicking on the navbar.
     */
    @Test
    public void testViewUserPage() {
        onViewLoaded(withId(R.id.main));
        onViewLoaded(withId(R.id.navigation_profile)).perform(click());
    }
}
