package com.example.skeddly;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotNull;
import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLoaded;

@RunWith(AndroidJUnit4.class)
public class UserInstrumentedTest {
    @Test
    public void testLoginUser() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        DatabaseHandler database = new DatabaseHandler(appContext);
        Authenticator authenticator = new Authenticator(appContext, database, new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser) {
                assertNotNull(loadedUser);
            }
        });
    }
}
