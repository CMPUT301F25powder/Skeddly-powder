package com.example.skeddly.business;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.PersonalInformation;
import com.example.skeddly.business.user.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for User and PersonalInformation business logic.
 */
public class UserUnitTest {

    private User user;
    private PersonalInformation personalInfo;

    @Before
    public void setUp() {
        user = new User();
        personalInfo = new PersonalInformation();
    }

    @Test
    public void addOwnedEvent_addsEventIdCorrectly() {
        Event mockEvent = new Event();
        mockEvent.setId("jacky161");

        assertTrue(user.getOwnedEvents().isEmpty());

        user.addOwnedEvent(mockEvent);

        assertEquals(1, user.getOwnedEvents().size());
        assertTrue(user.getOwnedEvents().contains("jacky161"));
    }


    @Test
    public void isFullyFilled_validationLogic() {
        assertFalse(personalInfo.isFullyFilled());

        personalInfo.setName("Jacky161");
        assertFalse(personalInfo.isFullyFilled());

        personalInfo.setEmail("jacky5@ualberta.com");
        assertTrue(personalInfo.isFullyFilled());

    }
}
