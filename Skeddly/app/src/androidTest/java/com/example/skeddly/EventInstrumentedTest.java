package com.example.skeddly;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.filters.LargeTest;

import com.example.skeddly.business.event.Event;

import org.junit.Test;

@LargeTest
public class EventInstrumentedTest extends BaseTest {
    @Test
    public void testViewEvent() {
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