package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class CallFromBellMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertEquals(Bell.BELL_6, composition.get().getCallFromBell());
    }

    @Test
    public void changeIsReflected() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setCallFromBell(Bell.BELL_1);

        assertEquals(Bell.BELL_1, composition.get().getCallFromBell());
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingHigherThanNumberOfBellsThrows() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setNumberOfBells(NumberOfBells.BELLS_8);
        composition.setCallFromBell(Bell.BELL_9);

        assertEquals(Bell.BELL_1, composition.get().getCallFromBell());
    }
}
