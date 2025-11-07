package com.example.skeddly;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaseTest {
    @Rule
    public ActivityScenarioRule<SignupActivity> signupActivityActivityScenarioRule = new ActivityScenarioRule<>(SignupActivity.class);

    private LoginIdlingResource loginIdlingResource;

    @Before
    public void setup() {
        signupActivityActivityScenarioRule.getScenario().onActivity(activity -> {
            loginIdlingResource = new LoginIdlingResource(activity);

            IdlingRegistry.getInstance().register(loginIdlingResource);
        });
    }

    @After
    public void unregisterIdlingResource() {
        if (loginIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(loginIdlingResource);
        }
    }
}
