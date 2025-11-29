package com.example.skeddly;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static com.example.skeddly.utilities.TestUtil.onViewLoaded;

import androidx.test.filters.LargeTest;

// Import BaseTest
import com.example.skeddly.utilities.BaseTest;

import org.junit.Test;

@LargeTest
public class AdminToolsInstrumentedTest extends BaseTest {

    @Test
    public void testAdmin_CanNavigateToToolsFragment() {
        onViewLoaded(R.id.navigation_tools).perform(click());

        onViewLoaded(R.id.btn_my_events).check(matches(isDisplayed()));
    }
}
