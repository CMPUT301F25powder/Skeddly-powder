package com.example.skeddly.business;

import static org.junit.Assert.assertEquals;

import com.example.skeddly.business.location.CustomLocation;

import org.junit.Test;

/**
 * Tests for {@link CustomLocation}.
 */
public class CustomLocationUnitTest {
    /**
     * Tests of the location string (lat, lon) is properly generated
     * - Should be generated to 2 decimal placesW
     */
    @Test
    public void testCustomLocationString() {
        CustomLocation customLocation = new CustomLocation(0.56f, 0.234f);

        assertEquals("(0.56, 0.23)", customLocation.toString());
    }
}
