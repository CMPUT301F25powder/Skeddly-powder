package com.example.skeddly;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.utilities.BaseTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminToolsInstrumentedTest extends BaseTest {

    private static final int STEP_DELAY_MS = 500;

    /**
     * Navigates to the Tools screen before each test.
     * BaseTest ensures the app is logged in and ready.
     */
    @Before
    public void navigateToAdminTools() throws InterruptedException {
        onView(withId(R.id.navigation_tools)).perform(click());
        Thread.sleep(STEP_DELAY_MS);
        onView(withId(R.id.btn_my_events)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToMyEventsAndBack() throws InterruptedException {
        onView(withId(R.id.btn_my_events)).perform(click());
        Thread.sleep(STEP_DELAY_MS);

        onView(withId(R.id.list_events)).check(matches(isDisplayed()));
        Thread.sleep(STEP_DELAY_MS);

        pressBack();
        Thread.sleep(STEP_DELAY_MS);

        onView(withId(R.id.btn_my_events)).check(matches(isDisplayed()));
    }

    @Test
    public void testImageGallery_AdminCanDeleteImage() throws InterruptedException {
        onView(withId(R.id.btn_img_gallery)).perform(click());
        Thread.sleep(STEP_DELAY_MS);
        onView(withId(R.id.uploadedImages)).check(matches(isDisplayed()));

        onData(is(instanceOf(Object.class)))
                .inAdapterView(withId(R.id.uploadedImages))
                .atPosition(0)
                .perform(longClick());

        Thread.sleep(STEP_DELAY_MS);

        onView(withId(R.id.deleteSelectedBtn)).perform(click());
        Thread.sleep(STEP_DELAY_MS);

        onView(withId(R.id.imageSelectHeader)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewUsers_CanFilterByOrganizer() throws InterruptedException {
        onView(withId(R.id.btn_view_user)).perform(click());
        Thread.sleep(STEP_DELAY_MS);

        onView(ViewMatchers.withId(R.id.list_view_users)).check(matches(isDisplayed()));

        onView(withId(R.id.switch_organizers_only)).perform(click());
        Thread.sleep(STEP_DELAY_MS);

        onView(withId(R.id.switch_organizers_only)).check(matches(isDisplayed()));

        onView(withId(R.id.switch_organizers_only)).perform(click());
    }

    @Test
    public void testNotificationLogs_CanViewNotifs() throws InterruptedException {
        onView(withId(R.id.btn_log_notification)).perform(click());
        Thread.sleep(STEP_DELAY_MS);
        onView(withId(R.id.list_notifications)).check(matches(isDisplayed()));

    }
}
