package com.example.skeddly;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.skeddly.utilities.TestUtil.onViewLoaded;
import static com.example.skeddly.utilities.TestUtil.typeSearchViewText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.filters.LargeTest;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.utilities.BaseTest;
import com.example.skeddly.utilities.TestUtil;

import org.junit.Test;

/**
 * Tests {@link Event} related UI elements.
 */
@LargeTest
public class EventInstrumentedTest extends BaseTest {
    public void createTestEvent() {

    }

    /**
     * Tests creating events
     */
    @Test
    public void searchEvent() {
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Wait for first event to appear
        onViewLoaded(withId(R.id.single_event_item));

        onView(withId(R.id.search_events)).perform(typeSearchViewText("one more"));

        onViewLoaded(withId(R.id.list_events));

        onViewLoaded(withText("One more event"));
    }

    /**
     * Tests if the content card with information about an {@link Event} is properly displayed.
     */
    @Test
    public void testViewEvent() {
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Wait for first event to appear
        onViewLoaded(withId(R.id.single_event_item));

        onData(is(instanceOf(Event.class)))
                .inAdapterView(withId(R.id.list_events))
                .atPosition(0)
                .onChildView(withId(R.id.single_event_item))
                .onChildView(withId(R.id.btn_view_info))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.main_content_card)).check(matches(isDisplayed()));
    }

    /**
     * Tests if the QR code popup properly appears.
     */
    @Test
    public void testViewEventQRCode() {
        testViewEvent();

        onView(withId(R.id.btn_qr_code)).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.qr_popup)).check(matches(isDisplayed()));
    }
}
