package com.example.skeddly;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static com.example.skeddly.utilities.TestUtil.onViewLoaded;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.skeddly.business.database.repository.NotificationRepository;
import com.example.skeddly.business.database.repository.UserRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.notification.Notification;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.example.skeddly.utilities.BaseTest;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@LargeTest
public class AdminToolsInstrumentedTest extends BaseTest {

    /**
     * Navigates to the Tools screen before each test.
     * BaseTest ensures the app is logged in and ready.
     */
    @Before
    public void navigateToAdminTools() {
        onViewLoaded(withId(R.id.navigation_tools)).perform(click());
        onViewLoaded(withId(R.id.btn_my_events));
    }

    @Test
    public void testNavigateToMyEventsAndBack() {
        onView(withId(R.id.btn_my_events)).perform(click());

        onViewLoaded(withId(R.id.list_events));

        pressBack();

        onViewLoaded(withId(R.id.btn_my_events));
    }

    @Test
    public void testImageGallery_AdminCanDeleteImage() throws InterruptedException {
        onViewLoaded(withId(R.id.btn_img_gallery)).perform(click());
        onViewLoaded(withId(R.id.uploadedImages));
        onViewLoaded(withId(R.id.uploadedImage));

        onData(is(instanceOf(Object.class)))
                .inAdapterView(withId(R.id.uploadedImages))
                .atPosition(0)
                .perform(longClick());


        onViewLoaded(withId(R.id.deleteSelectedBtn)).perform(click());

        onViewLoaded(withId(R.id.imageSelectHeader));
    }

    @Test
    public void testViewUsers_ShowsAllUsersAndFiltersByOrganizer() throws ExecutionException, InterruptedException {
        onView(withId(R.id.btn_view_user)).perform(click());

        onViewLoaded(withId(R.id.list_view_users)).check(matches(isDisplayed()));

        UserRepository allUsersRepo = new UserRepository(FirebaseFirestore.getInstance());
        List<User> allUsersFromDb = Tasks.await(allUsersRepo.getAll());
        int allUsersCount = allUsersFromDb.size();

        onViewLoaded(withId(R.id.list_view_users)).check((view, noViewFoundException) -> {
            android.widget.ListView listView = (android.widget.ListView) view;
            Assert.assertEquals("ListView should show all users initially.", allUsersCount, listView.getAdapter().getCount());
        });

        onViewLoaded(withId(R.id.switch_organizers_only)).perform(click());

        List<User> organizersFromDb = allUsersFromDb.stream()
                .filter(user -> user.getPrivilegeLevel() == UserLevel.ORGANIZER)
                .collect(Collectors.toList());
        int organizerCount = organizersFromDb.size();

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.list_view_users)).check((view, noViewFoundException) -> {
            android.widget.ListView listView = (android.widget.ListView) view;
            Assert.assertEquals("ListView should only show organizers after filtering.", organizerCount, listView.getAdapter().getCount());
        });

        onView(withId(R.id.switch_organizers_only)).perform(click());
    }

    @Test
    public void testNotificationLogs_CanViewNotifs() throws ExecutionException, InterruptedException {
        onViewLoaded(withId(R.id.btn_log_notification)).perform(click());
        onViewLoaded(withId(R.id.list_notifications));

        NotificationRepository notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());
        List<Notification> notificationsFromDb = Tasks.await(notificationRepository.getAll());
        int dbCount = notificationsFromDb.size();

        onView(withId(R.id.list_notifications)).check((view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            android.widget.ListView listView = (android.widget.ListView) view;
            android.widget.Adapter adapter = listView.getAdapter();
            Assert.assertEquals("ListView item count should match Firestore count.", dbCount, adapter.getCount());
        });
    }
}
