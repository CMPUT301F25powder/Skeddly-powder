package com.example.skeddly.utilities;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.skeddly.SignupActivity;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.event.Event;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

/**
 * A base testing class that initializes all activities crucial for testing UI.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public abstract class BaseTest {
    @Rule
    public ActivityScenarioRule<SignupActivity> signupActivityActivityScenarioRule = new ActivityScenarioRule<>(SignupActivity.class);

    private LoginIdlingResource loginIdlingResource;
    protected EventRepository eventRepository;
    protected Event event1;
    protected Event event2;

    /**
     * Initial setup: waits for {@link com.example.skeddly.MainActivity} to load after {@link SignupActivity} using {@link LoginIdlingResource}.
     */
    @Before
    public void setup() throws ExecutionException, InterruptedException {
        signupActivityActivityScenarioRule.getScenario().onActivity(activity -> {
            loginIdlingResource = new LoginIdlingResource(activity);

            IdlingRegistry.getInstance().register(loginIdlingResource);

            this.event1 = TestUtil.createMockEvent("MockEvent1", null);
            this.event2 = TestUtil.createMockEvent("Op", null);
            eventRepository = new EventRepository(FirebaseFirestore.getInstance());
            eventRepository.set(event1);
            eventRepository.set(event2);
        });



    }

    /**
     * Unregisters the {@link LoginIdlingResource} used in {@link BaseTest#setup} when done.
     */
    public void unregisterIdlingResource() {
        if (loginIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(loginIdlingResource);
        }
    }

    @After
    public void tearDown() {
        eventRepository.delete(event1.getId());
        eventRepository.delete(event2.getId());
        unregisterIdlingResource();
    }
}
