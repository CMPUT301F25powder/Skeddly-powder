package com.example.skeddly;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.skeddly.utilities.TestUtil.onViewLoaded;
import static org.junit.Assert.assertEquals;

import android.widget.Adapter;
import android.widget.ListView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.repository.TicketRepository;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.utilities.BaseTest;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ProfileFragmentInstrumentedTest extends BaseTest {

    private User currentUser;
    @Before
    public void navigateToProfileScreen() {
        onViewLoaded(withId(R.id.navigation_profile)).perform(click());
        onViewLoaded(withId(R.id.header_profile));

        this.currentUser = MainActivity.getInstance().getUser();

    }

    @Test
    public void testNavigateToEventHistoryAndVerifyContent() throws ExecutionException, InterruptedException {
        onViewLoaded(withId(R.id.btn_event_history)).perform(click());

        onViewLoaded(withId(R.id.list_events));

        TicketRepository ticketRepository = new TicketRepository(FirebaseFirestore.getInstance(), null, currentUser.getId());
        List<Ticket> ticketsFromDb = Tasks.await(ticketRepository.getAll());
        int expectedCount = ticketsFromDb.size();

        DatabaseHandler databaseHandler = new DatabaseHandler();
        CustomLocation location = new CustomLocation(53.5, -113.5);
        event1.join(databaseHandler, currentUser.getPersonalInformation(), currentUser.getId(), location);

        Thread.sleep(5000);

        onViewLoaded(withId(R.id.list_events));
        onViewLoaded(withId(R.id.item_event_history_layout));

        onView(withId(R.id.list_events)).check((view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            ListView listView = (ListView) view;
            Adapter adapter = listView.getAdapter();
            assertEquals("Event history count should match user's ticket count.", expectedCount, adapter.getCount());
        });
    }

    @Test
    public void testPersonalInfoEditing() throws InterruptedException {
        // Generate test data
        String newName = "Test User";
        String newEmail = "test";
        String newPhone = "555-123-4567";

        onViewLoaded(withId(R.id.btn_personal_info)).perform(click());

        onViewLoaded(withId(R.id.edit_full_name)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_full_name)).perform(ViewActions.replaceText(newName));
        onView(withId(R.id.edit_email)).perform(ViewActions.replaceText(newEmail));
        onView(withId(R.id.edit_phone_number)).perform(ViewActions.replaceText(newPhone));

        onViewLoaded(withId(R.id.include)).perform(click());

        onViewLoaded(withId(R.id.header_profile));

        onView(withId(R.id.profile_name)).check(matches(ViewMatchers.withText(newName)));
        onView(withId(R.id.profile_email)).check(matches(ViewMatchers.withText(newEmail)));
        onView(withId(R.id.profile_phone)).check(matches(ViewMatchers.withText(newPhone)));
    }


}
