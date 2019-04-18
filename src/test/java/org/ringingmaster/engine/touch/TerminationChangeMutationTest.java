package org.ringingmaster.engine.touch;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.MethodBuilder;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TerminationChangeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(Optional.empty(), touch.get().getTerminationChange());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        touch.setTerminationChange(MethodBuilder.parse(NumberOfBells.BELLS_6, "654123"));
        assertEquals("654123", touch.get().getTerminationChange().get().getDisplayString(false));

        touch.removeTerminationChange();
        assertFalse(touch.get().getTerminationChange().isPresent());

    }

    @Test(expected = IllegalArgumentException.class)
    public void addingStartChangeWithIncorrectNumberThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setStartChange(MethodBuilder.parse(NumberOfBells.BELLS_5, "54123"));

    }
}