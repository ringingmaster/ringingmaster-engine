package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_CIRCULARITY_MAX;


/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationMaxCircularityMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(TERMINATION_MAX_CIRCULARITY_INITIAL_VALUE, composition.get().getTerminationMaxCircularity());
    }

    @Test
    public void changeIsReflected(){

        ObservableComposition composition = new ObservableComposition();

        composition.setTerminationMaxCircularComposition(10);
        assertEquals(10, composition.get().getTerminationMaxCircularity());

        composition.setTerminationMaxCircularComposition(TERMINATION_MAX_CIRCULARITY_MAX);
        assertEquals(TERMINATION_MAX_CIRCULARITY_MAX, composition.get().getTerminationMaxCircularity());

        composition.setTerminationMaxCircularComposition(1);
        assertEquals(1, composition.get().getTerminationMaxCircularity());

    }

    @Test (expected = IllegalArgumentException.class)
    public void setting0OrLowerThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTerminationMaxCircularComposition(0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTerminationMaxCircularComposition(TERMINATION_MAX_CIRCULARITY_MAX +1);
    }

}
