package org.ringingmaster.engine.composition;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_PARTS_MAX;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationMaxPartsMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(Optional.empty(), composition.get().getTerminationMaxParts());
    }

    @Test
    public void changeIsReflected(){

        ObservableComposition composition = new ObservableComposition();

        composition.setTerminationMaxParts(10);
        assertEquals(Optional.of(10), composition.get().getTerminationMaxParts());

        composition.setTerminationMaxParts(TERMINATION_MAX_PARTS_MAX);
        assertEquals(Optional.of(TERMINATION_MAX_PARTS_MAX), composition.get().getTerminationMaxParts());

        composition.setTerminationMaxParts(1);
        assertEquals(Optional.of(1), composition.get().getTerminationMaxParts());

        composition.removeTerminationMaxParts();
        assertEquals(Optional.empty(), composition.get().getTerminationMaxParts());
    }

    @Test (expected = IllegalArgumentException.class)
    public void setting0OrLowerThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTerminationMaxParts(0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTerminationMaxParts(TERMINATION_MAX_PARTS_MAX +1);
    }

}
