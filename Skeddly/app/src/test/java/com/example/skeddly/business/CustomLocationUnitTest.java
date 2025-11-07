package com.example.skeddly.business;

import static org.junit.Assert.assertEquals;

import com.example.skeddly.business.location.CustomLocation;

import org.junit.Test;

public class CustomLocationUnitTest {
    @Test
    public void testCustomLocationString() {
        CustomLocation customLocation = new CustomLocation(0.234f, 0.56f);

        assertEquals("(0.23, 0.56)", customLocation.toString());
    }
}
