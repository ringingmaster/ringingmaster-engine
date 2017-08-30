package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.touch.newcontainer.ObservableTouch.TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE;
import static org.ringingmaster.engine.touch.newcontainer.ObservableTouch.TERMINATION_MAX_CIRCULARITY_MAX;


/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationMaxCircularityMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE, touch.get().getTerminationMaxCircularity());
    }

    @Test
    public void changeIsReflected(){

        ObservableTouch touch = new ObservableTouch();

        touch.setTerminationMaxCircularTouch(10);
        assertEquals(10, touch.get().getTerminationMaxCircularity());

        touch.setTerminationMaxCircularTouch(TERMINATION_MAX_CIRCULARITY_MAX);
        assertEquals(TERMINATION_MAX_CIRCULARITY_MAX, touch.get().getTerminationMaxCircularity());

        touch.setTerminationMaxCircularTouch(1);
        assertEquals(1, touch.get().getTerminationMaxCircularity());

    }

    @Test (expected = IllegalStateException.class)
    public void setting0OrLowerThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTerminationMaxCircularTouch(0);
    }

    @Test (expected = IllegalStateException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTerminationMaxCircularTouch(TERMINATION_MAX_CIRCULARITY_MAX +1);
    }

}
