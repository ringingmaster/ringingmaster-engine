package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NumberOfBellsMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch observableTouch = new ObservableTouch();

        assertEquals(NumberOfBells.BELLS_6, observableTouch.get().getNumberOfBells());
    }

    @Test
    public void changeIsReflectedInNonOptionalFields() throws Exception {

        ObservableTouch observableTouch = new ObservableTouch();
        observableTouch.setNumberOfBells(NumberOfBells.BELLS_20);

        assertEquals(NumberOfBells.BELLS_20, observableTouch.get().getNumberOfBells());
        assertEquals(NumberOfBells.BELLS_20, observableTouch.get().getStartChange().getNumberOfBells());
    }

    @Test
    public void whenActiveNotationIsNoLongerValidBestAlternativeIsSelected() throws Exception {

        //TODO - many tests
        fail();
    }

    @Test
    public void whenTerminationChangeExistsChangeIsReflected() throws Exception {

        //TODO
        fail();
    }

    @Test
    public void whenStartNotationExistsChangeIsReflected() throws Exception {

        //TODO
        fail();
    }

}
