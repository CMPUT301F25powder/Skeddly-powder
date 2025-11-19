package com.example.skeddly.utilities;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.skeddly.SignupActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * A base testing class that initializes all activities crucial for testing UI.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaseTest {
    @Rule
    public ActivityScenarioRule<SignupActivity> signupActivityActivityScenarioRule = new ActivityScenarioRule<>(SignupActivity.class);

    private LoginIdlingResource loginIdlingResource;

    /**
     * Initial setup: waits for {@link com.example.skeddly.MainActivity} to load after {@link SignupActivity} using {@link LoginIdlingResource}.
     */
    @Before
    public void setup() {
        signupActivityActivityScenarioRule.getScenario().onActivity(activity -> {
            loginIdlingResource = new LoginIdlingResource(activity);

            IdlingRegistry.getInstance().register(loginIdlingResource);
        });
    }

    /**
     * Unregisters the {@link LoginIdlingResource} used in {@link BaseTest#setup} when done.
     */
    @After
    public void unregisterIdlingResource() {
        if (loginIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(loginIdlingResource);
        }
    }
}
