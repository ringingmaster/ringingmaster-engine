package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_PART_CIRCULARITY_INITIAL_VALUE;
import static org.ringingmaster.engine.composition.MutableComposition.TERMINATION_MAX_PART_CIRCULARITY_MAX;


/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class TerminationMaxCircularityMutationTest {

    @Test
    public void hasCorrectDefault() {

        MutableComposition composition = new MutableComposition();

        assertEquals(TERMINATION_MAX_PART_CIRCULARITY_INITIAL_VALUE, composition.get().getTerminationMaxPartCircularity());
    }

    @Test
    public void changeIsReflected(){

        MutableComposition composition = new MutableComposition();

        composition.setTerminationMaxPartCircularity(10);
        assertEquals(10, composition.get().getTerminationMaxPartCircularity());

        composition.setTerminationMaxPartCircularity(TERMINATION_MAX_PART_CIRCULARITY_MAX);
        assertEquals(TERMINATION_MAX_PART_CIRCULARITY_MAX, composition.get().getTerminationMaxPartCircularity());

        composition.setTerminationMaxPartCircularity(1);
        assertEquals(1, composition.get().getTerminationMaxPartCircularity());

    }

    @Test (expected = IllegalArgumentException.class)
    public void setting0OrLowerThrows() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setTerminationMaxPartCircularity(0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setTerminationMaxPartCircularity(TERMINATION_MAX_PART_CIRCULARITY_MAX +1);
    }

}
