package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.ObservableComposition.START_AT_ROW_MAX;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class StartAtRowMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(0, composition.get().getStartAtRow());
    }

    @Test
    public void changeIsReflected(){

        ObservableComposition composition = new ObservableComposition();

        composition.setStartAtRow(10);
        assertEquals(10, composition.get().getStartAtRow());

        composition.setStartAtRow(START_AT_ROW_MAX);
        assertEquals(START_AT_ROW_MAX, composition.get().getStartAtRow());

        composition.setStartAtRow(0);
        assertEquals(0, composition.get().getStartAtRow());
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingMinusOneOrLowerThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setStartAtRow(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setStartAtRow(START_AT_ROW_MAX +1);
    }

}
