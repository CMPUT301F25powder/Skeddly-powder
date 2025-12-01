package com.example.skeddly;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static com.example.skeddly.utilities.TestUtil.onViewLoaded;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.widget.ListView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.skeddly.business.database.repository.NotificationRepository;
import com.example.skeddly.business.notification.Notification;
import com.example.skeddly.business.notification.NotificationType;
import com.example.skeddly.business.user.User;
import com.example.skeddly.utilities.BaseTest;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Instrumented tests for the Inbox screen and its functionality.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class InboxInstrumentedTest extends BaseTest {

    private User currentUser;
    private NotificationRepository notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());


    /**
     * Navigates to the inbox screen and retrieves the current user before each test.
     */
    @Before
    public void navigateToInboxScreen() throws InterruptedException, ExecutionException {
        onViewLoaded(withId(R.id.navigation_inbox)).perform(click());
        onViewLoaded(withId(R.id.list_notifications));

        Thread.sleep(3500);
        this.currentUser = MainActivity.getInstance().getUser();

        List<Task<Void>> saveTasks = new ArrayList<>();

        // Create one notification of each type
        Notification messageNotif = new Notification("Message Title", "This is a test message.", currentUser.getId(), NotificationType.MESSAGES);
        saveTasks.add(notificationRepository.set(messageNotif));

        Notification registrationNotif = new Notification("Registration Title", "This is a registration message.", currentUser.getId(), NotificationType.REGISTRATION);
        saveTasks.add(notificationRepository.set(registrationNotif));

        Notification systemNotif = new Notification("System Title", "This is a system message.", currentUser.getId(), NotificationType.SYSTEM);
        saveTasks.add(notificationRepository.set(systemNotif));

        Tasks.await(Tasks.whenAll(saveTasks));

        onViewLoaded(withId(R.id.navigation_inbox)).perform(click());

        Thread.sleep(2000);
        onViewLoaded(withId(R.id.list_notifications));
    }

    /**
     * Clears all notifications for the current user after each test.
     */
    @After
    public void clearInboxAfter() throws ExecutionException, InterruptedException {
        Task<List<Notification>> allUserNotificationsTask = new NotificationRepository(FirebaseFirestore.getInstance(), currentUser.getId()).getAll();
        List<Notification> notificationsToDelete = Tasks.await(allUserNotificationsTask);

        List<Task<Void>> deleteTasks = new ArrayList<>();
        for (Notification notification : notificationsToDelete) {
            deleteTasks.add(notificationRepository.delete(notification.getId()));
        }

        if (!deleteTasks.isEmpty()) {
            Tasks.await(Tasks.whenAll(deleteTasks));
        }
    }

    /**
     * Verifies that the "All" filter correctly displays all notifications for the current user.
     */
    @Test
    public void testAllNotificationsFilter() throws ExecutionException, InterruptedException {
        NotificationRepository allRepo = new NotificationRepository(FirebaseFirestore.getInstance(), currentUser.getId());
        List<Notification> allNotifications = Tasks.await(allRepo.getAll());
        int expectedCount = allNotifications.size();

        Thread.sleep(1500);

        onView(withId(R.id.list_notifications)).check((view, noViewFoundException) -> {
            ListView listView = (ListView) view;
            assertEquals("ListView count should match 'All' notifications count.", expectedCount, listView.getAdapter().getCount());
        });
    }

    /**
     * Verifies that the "Messages" filter correctly displays only message notifications.
     */
    @Test
    public void testMessagesFilter() throws ExecutionException, InterruptedException {
        onViewLoaded(withId(R.id.btn_messages)).perform(click());

        NotificationRepository messagesRepo = new NotificationRepository(FirebaseFirestore.getInstance(), currentUser.getId(), NotificationType.MESSAGES);
        List<Notification> messageNotifications = Tasks.await(messagesRepo.getAll());
        int expectedCount = messageNotifications.size();

        Thread.sleep(1500);

        onView(withId(R.id.list_notifications)).check((view, noViewFoundException) -> {
            ListView listView = (ListView) view;
            assertEquals("ListView count should match 'Messages' notifications count.", expectedCount, listView.getAdapter().getCount());
        });
    }

    /**
     * Verifies that the "Registration" filter correctly displays only registration notifications.
     */
    @Test
    public void testRegistrationFilter() throws ExecutionException, InterruptedException {
        onViewLoaded(withId(R.id.btn_registration)).perform(click());

        NotificationRepository regRepo = new NotificationRepository(FirebaseFirestore.getInstance(), currentUser.getId(), NotificationType.REGISTRATION);
        List<Notification> registrationNotifications = Tasks.await(regRepo.getAll());
        int expectedCount = registrationNotifications.size();

        Thread.sleep(1500);

        onView(withId(R.id.list_notifications)).check((view, noViewFoundException) -> {
            ListView listView = (ListView) view;
            assertEquals("ListView count should match 'Registration' notifications count.", expectedCount, listView.getAdapter().getCount());
        });
    }

    /**
     * Verifies that the "System" filter correctly displays only system notifications.
     */
    @Test
    public void testSystemFilter() throws ExecutionException, InterruptedException {
        onViewLoaded(withId(R.id.btn_system)).perform(click());

        NotificationRepository systemRepo = new NotificationRepository(FirebaseFirestore.getInstance(), currentUser.getId(), NotificationType.SYSTEM);
        List<Notification> systemNotifications = Tasks.await(systemRepo.getAll());
        int expectedCount = systemNotifications.size();

        Thread.sleep(1500);


        onView(withId(R.id.list_notifications)).check((view, noViewFoundException) -> {
            ListView listView = (ListView) view;
            assertEquals("ListView count should match 'System' notifications count.", expectedCount, listView.getAdapter().getCount());
        });
    }
}
