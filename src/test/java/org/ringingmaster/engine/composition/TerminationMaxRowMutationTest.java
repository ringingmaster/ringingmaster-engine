package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_ROWS_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.ObservableComposition.TERMINATION_MAX_ROWS_MAX;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationMaxRowMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(TERMINATION_MAX_ROWS_INITIAL_VALUE, composition.get().getTerminationMaxRows());
    }

    @Test
    public void changeIsReflected(){

        ObservableComposition composition = new ObservableComposition();

        composition.setTerminationMaxRows(10);
        assertEquals(10, composition.get().getTerminationMaxRows());

        composition.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX);
        assertEquals(TERMINATION_MAX_ROWS_MAX, composition.get().getTerminationMaxRows());

        composition.setTerminationMaxRows(1);
        assertEquals(1, composition.get().getTerminationMaxRows());
    }

    @Test (expected = IllegalArgumentException.class)
    public void setting0OrLowerThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTerminationMaxRows(0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTerminationMaxRows(TERMINATION_MAX_ROWS_MAX +1);
    }

}
