package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.MutableComposition.START_AT_ROW_MAX;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class StartAtRowMutationTest {

    @Test
    public void hasCorrectDefault() {

        MutableComposition composition = new MutableComposition();

        assertEquals(0, composition.get().getStartAtRow());
    }

    @Test
    public void changeIsReflected(){

        MutableComposition composition = new MutableComposition();

        composition.setStartAtRow(10);
        assertEquals(10, composition.get().getStartAtRow());

        composition.setStartAtRow(START_AT_ROW_MAX);
        assertEquals(START_AT_ROW_MAX, composition.get().getStartAtRow());

        composition.setStartAtRow(0);
        assertEquals(0, composition.get().getStartAtRow());
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingMinusOneOrLowerThrows() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setStartAtRow(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setStartAtRow(START_AT_ROW_MAX +1);
    }

}
