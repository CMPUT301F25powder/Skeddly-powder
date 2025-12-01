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
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.utilities.BaseTest;
import com.example.skeddly.utilities.TestUtil;

import org.junit.After;
import org.junit.Test;

/**
 * Tests {@link Event} related UI elements.
 */
@LargeTest
public class EventInstrumentedTest extends BaseTest {
    /**
     * Tests creating events
     */
    @Test
    public void searchEvent() {
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Wait for first event to appear
        onViewLoaded(withId(R.id.single_event_item));

        onViewLoaded(withId(R.id.search_events)).perform(typeSearchViewText("MockEvent"));

        onViewLoaded(withId(R.id.list_events));

        onViewLoaded(withText("MockEvent1"));
    }

    /**
     * Tests if the content card with information about an {@link Event} is properly displayed.
     */
    @Test
    public void testViewEvent() {
        onViewLoaded(withId(R.id.main));

        // Wait for first event to appear
        onViewLoaded(withId(R.id.single_event_item));

        onData(is(instanceOf(Event.class)))
                .inAdapterView(withId(R.id.list_events))
                .atPosition(0)
                .onChildView(withId(R.id.single_event_item))
                .onChildView(withId(R.id.btn_view_info))
                .check(matches(isDisplayed()))
                .perform(click());

        onViewLoaded(withId(R.id.main_content_card));
    }

    /**
     * Tests if the QR code popup properly appears.
     */
    @Test
    public void testViewEventQRCode() {
        testViewEvent();

        onViewLoaded(withId(R.id.btn_qr_code)).perform(click());

        onViewLoaded(withId(R.id.qr_popup));
    }

    /**
     * Tests that event details are displayed correctly and that the map popup can be opened.
     */
    @Test
    public void testEventDetailsAreDisplayedCorrectly() {
        testViewEvent();

        onViewLoaded(withId(R.id.value_event_title));
        onViewLoaded(withId(R.id.value_event_description));
        onViewLoaded(withId(R.id.value_attendee_limit));
    }

}
