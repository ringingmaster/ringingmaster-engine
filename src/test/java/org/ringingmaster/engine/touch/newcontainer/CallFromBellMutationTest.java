package org.ringingmaster.engine.touch.newcontainer;

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

        ObservableTouch touch = new ObservableTouch();

        assertEquals(Bell.BELL_6, touch.get().getCallFromBell());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setCallFromBell(Bell.BELL_1);

        assertEquals(Bell.BELL_1, touch.get().getCallFromBell());
    }

    @Test
    public void settingHigherThanNumberOfBellsThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(NumberOfBells.BELLS_8);
        touch.setCallFromBell(Bell.BELL_9);

        assertEquals(Bell.BELL_1, touch.get().getCallFromBell());
    }
}
