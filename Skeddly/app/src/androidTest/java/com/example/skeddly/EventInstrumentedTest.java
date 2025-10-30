package com.example.skeddly;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.skeddly.business.Event;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLoaded;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class EventInstrumentedTest {
    @Test
    public void testSaveEvent() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHandler database = new DatabaseHandler(appContext);

        Event event = new Event();
        event.setId(String.valueOf(UUID.randomUUID()));

        database.getEventsPath().setValue(event);

//        assertEquals(event, database.getEventsPath().child(event.getId()));
    }
}
