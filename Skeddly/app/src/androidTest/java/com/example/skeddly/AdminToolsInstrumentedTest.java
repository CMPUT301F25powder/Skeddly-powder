package com.example.skeddly;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

// Import BaseTest
import com.example.skeddly.utilities.BaseTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminToolsInstrumentedTest {

    @Test
    public void testAdmin_CanNavigateToToolsFragment() throws InterruptedException {
        ActivityScenario.launch(MainActivity.class);
        Thread.sleep(10000);

        onView(withId(R.id.navigation_tools)).perform(click());
        onView(withId(R.id.btn_my_events)).check(matches(isDisplayed()));
    }
}
