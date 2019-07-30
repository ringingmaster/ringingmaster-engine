package org.ringingmaster.engine;

import org.junit.Test;
import org.ringingmaster.engine.notation.Place;

import static org.junit.Assert.assertEquals;

public class NumberOfBellsTest {

    @Test
    public void iteratorFunctions() {
        int i = 0;
        for (final Place place : NumberOfBells.MAX) {
            assertEquals(place, Place.valueOf(i));
            i++;
        }
        assertEquals(i, 30);
    }

}
