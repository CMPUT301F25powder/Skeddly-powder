package com.example.skeddly.utilities;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;

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
        byte[] image = (imageBytes != null) ? imageBytes : "default_mock_image".getBytes();

        return new Event(detail, schedule, location, "mockOrganizerId", 100, false, image);
    }

    /**
     * Creates a generic mock User.
     * @param name The name of the user.
     * @param level The privilege level of the user (ENTRANT, ORGANIZER, ADMIN).
     * @return A new User object.
     */
    public static User createMockUser(String name, UserLevel level) throws ExecutionException, InterruptedException {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        UserRepository userRepository = new UserRepository(mFirestore);

        PersonalInformation info = new PersonalInformation(name, name.toLowerCase() + "@test.com", "555-123-4567");
        User user = new User(info, level);


        Tasks.await(userRepository.set(user));

        return user;
    }

    /**
     * Pauses an espresso test until a view appears
     * Particularly useful for ListViews with lots of items/images
     * Credit to <a href="https://www.repeato.app/espresso-wait-for-element/">Stephan Petzl</a>
     * @param viewId int
     * @param timeout long
     * @return ViewAction
     */
    public static ViewAction waitForView(int viewId, long timeout) {
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
                Matcher<View> viewMatcher = withId(viewId);

                while (System.currentTimeMillis() < end) {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        if (viewMatcher.matches(child)) {
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
}
