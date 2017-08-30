package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.touch.container.Touch.TERMINATION_MAX_PARTS_MAX;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationMaxPartsMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(Optional.empty(), touch.get().getTerminationMaxParts());
    }

    @Test
    public void changeIsReflected(){

        ObservableTouch touch = new ObservableTouch();

        touch.setTerminationMaxParts(10);
        assertEquals(Optional.of(10), touch.get().getTerminationMaxParts());

        touch.setTerminationMaxParts(TERMINATION_MAX_PARTS_MAX);
        assertEquals(Optional.of(TERMINATION_MAX_PARTS_MAX), touch.get().getTerminationMaxParts());

        touch.setTerminationMaxParts(1);
        assertEquals(Optional.of(1), touch.get().getTerminationMaxParts());

        touch.removeTerminationMaxParts();
        assertEquals(Optional.empty(), touch.get().getTerminationMaxParts());
    }

    @Test (expected = IllegalStateException.class)
    public void setting0OrLowerThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTerminationMaxParts(0);
    }

    @Test (expected = IllegalStateException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTerminationMaxParts(TERMINATION_MAX_PARTS_MAX +1);
    }

}
