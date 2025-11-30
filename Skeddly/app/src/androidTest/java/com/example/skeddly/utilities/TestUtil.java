package com.example.skeddly.utilities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.widget.SearchView;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;

import com.example.skeddly.R;
import com.example.skeddly.business.database.repository.UserRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;
import com.example.skeddly.business.user.PersonalInformation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Utility class for creating mock objects for instrumentation tests.
 */
public class TestUtil {

    /**
     * Creates a mock Event with default data and a specified name and image.
     * @param eventName The name for the mock event.
     * @param imageBytes The byte array for the event's image. Can be null.
     * @return A new Event object.
     */
    public static Event createMockEvent(String eventName, byte[] imageBytes) {
        LocalDateTime now = LocalDateTime.now();

        EventDetail detail = new EventDetail(eventName, "A mock description.", "Mock entry criteria.");
        EventSchedule schedule = new EventSchedule(now.plusDays(1), now.plusDays(2), now, now.plusHours(12));
        LatLng location = new LatLng(53.5, -113.5);

        // The image can be null if not needed for a specific test
        byte[] image = (imageBytes != null) ? imageBytes : "".getBytes();

        return new Event(detail, schedule, location, "mockOrganizerId", 100, false, image);
    }

    /**
     * Pauses an espresso test until a view appears
     * Particularly useful for ListViews with lots of items/images
     * Credit to <a href="https://www.repeato.app/espresso-wait-for-element/">Stephan Petzl</a>
     * @param viewMatcher Matcher<View>
     * @param timeout long
     * @return ViewAction
     */
    public static ViewAction waitForView(Matcher<View> viewMatcher, long timeout) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "waits for view to load before executing the next steps";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();

                long start = System.currentTimeMillis();
                long end = start + timeout;

                while (System.currentTimeMillis() < end) {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        if (viewMatcher.matches(child) && child.getVisibility() == View.VISIBLE) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(100);
                }

                throw new PerformException.Builder()
                        .withCause(new TimeoutException())
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .build();
            }
        };
    }

    /**
     * Waits for a view to load before continuing.
     * DO NOT use {@code .check(matches(isDisplayed()))} with this method as this is already assumed when view is loaded.
     * Timeout is 10000 ms by default.
     * @param viewMatcher Matcher<View>
     * @return ViewInteraction
     */
    public static ViewInteraction onViewLoaded(Matcher<View> viewMatcher) {
        return onViewLoaded(viewMatcher, 10000);
    }

    /**
     * Waits for a view to load before continuing.
     * DO NOT use {@code .check(matches(isDisplayed()))} with this method as this is already assumed when view is loaded.
     * If the time it takes to load exceeds the timeout, it will fail.
     * @param viewMatcher Matcher<View>
     * @param timeout int
     * @return ViewInteraction
     */
    public static ViewInteraction onViewLoaded(Matcher<View> viewMatcher, long timeout) {
        onView(isRoot())
                .perform(waitForView(viewMatcher, timeout));

        return onView(viewMatcher);
    }

    /**
     * Types text into a SearchView
     * https://stackoverflow.com/a/48037073
     * @param text String
     * @return ViewAction
     */
    public static ViewAction typeSearchViewText(final String text){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text, true);
            }
        };
    }
}
