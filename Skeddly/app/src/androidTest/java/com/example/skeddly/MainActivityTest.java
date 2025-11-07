package com.example.skeddly;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
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

    @Test
    public void testFindEvent() {
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        onData(is(instanceOf(Event.class)))
                .inAdapterView(withId(R.id.list_events))
                .atPosition(0)
                .onChildView(withId(R.id.single_event_item))
                .onChildView(withId(R.id.button_view_info))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.main_content_card)).check(matches(isDisplayed()));
    }
}