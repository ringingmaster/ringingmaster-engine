package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.touch.newcontainer.ObservableTouch.TERMINATION_MAX_ROWS_INITIAL_VALUE;
import static org.ringingmaster.engine.touch.newcontainer.ObservableTouch.TERMINATION_MAX_ROWS_MAX;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationMaxRowMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(TERMINATION_MAX_ROWS_INITIAL_VALUE, touch.get().getTerminationMaxRows());
    }

    @Test
    public void changeIsReflected(){

        ObservableTouch touch = new ObservableTouch();

        touch.setTerminationMaxRows(10);
        assertEquals(10, touch.get().getTerminationMaxRows());

        touch.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX);
        assertEquals(TERMINATION_MAX_ROWS_MAX, touch.get().getTerminationMaxRows());

        touch.setTerminationMaxRows(1);
        assertEquals(1, touch.get().getTerminationMaxRows());
    }

    @Test (expected = IllegalStateException.class)
    public void setting0OrLowerThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTerminationMaxRows(0);
    }

    @Test (expected = IllegalStateException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX +1);
    }

}
