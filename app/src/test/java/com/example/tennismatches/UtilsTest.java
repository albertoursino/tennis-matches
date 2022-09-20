package com.example.tennismatches;

import static com.example.tennismatches.Utils.createUniqueId;
import static org.junit.Assert.assertNotEquals;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {
    public void testCreateUniqueId() {
        String id1 = createUniqueId("MAT");
        String id2 = createUniqueId("MAT");
        assertNotEquals(id1, id2);
    }
}