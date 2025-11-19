package com.example.skeddly.business;

import static org.junit.Assert.assertEquals;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.DatabaseObjects;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tests {@link DatabaseObjects} and its functionality.
 */
public class DatabaseObjectsUnitTest {
    /**
     * Tests if {@link DatabaseObjects#getIds()} produces an accurate {@link java.util.ArrayList} of ids that are {@link String}s.
     */
    @Test
    public void testDatabaseObjectsIds() {
        DatabaseObjects databaseObjects = new DatabaseObjects();

        DatabaseObject obj1 = new DatabaseObject();
        DatabaseObject obj2 = new DatabaseObject();
        DatabaseObject obj3 = new DatabaseObject();

        databaseObjects.addAll(Arrays.asList(obj1, obj2, obj3));

        ArrayList<String> testIds = new ArrayList<>(Arrays.asList(obj1.getId(), obj2.getId(), obj3.getId()));

        assertEquals(testIds, databaseObjects.getIds());
    }
}
