package com.example.skeddly;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

// Import BaseTest
import com.example.skeddly.utilities.BaseTest;
import com.example.skeddly.utilities.TestUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
public class AdminToolsInstrumentedTest extends BaseTest {

    @Test
    public void testAdmin_CanNavigateToToolsFragment() {
        onView(isRoot())
                .perform(TestUtil.waitForView(R.id.navigation_tools, 10000));

        onView(withId(R.id.navigation_tools)).perform(click());

        onView(isRoot())
                .perform(TestUtil.waitForView(R.id.btn_my_events, 10000));

        onView(withId(R.id.btn_my_events)).check(matches(isDisplayed()));
    }
}
