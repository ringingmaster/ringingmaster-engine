package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CallFromBellMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(Bell.BELL_6, composition.get().getCallFromBell());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setCallFromBell(Bell.BELL_1);

        assertEquals(Bell.BELL_1, composition.get().getCallFromBell());
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingHigherThanNumberOfBellsThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(NumberOfBells.BELLS_8);
        composition.setCallFromBell(Bell.BELL_9);

        assertEquals(Bell.BELL_1, composition.get().getCallFromBell());
    }
}
