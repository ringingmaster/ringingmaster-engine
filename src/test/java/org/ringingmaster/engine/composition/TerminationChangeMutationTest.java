package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.MethodBuilder;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class TerminationChangeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertEquals(Optional.empty(), composition.get().getTerminationChange());
    }

    @Test
    public void changeIsReflected() throws Exception {

        MutableComposition composition = new MutableComposition();

        composition.setTerminationChange(MethodBuilder.parse(NumberOfBells.BELLS_6, "654123"));
        assertEquals("654123", composition.get().getTerminationChange().get().getDisplayString(false));

        composition.removeTerminationChange();
        assertFalse(composition.get().getTerminationChange().isPresent());

    }

    @Test(expected = IllegalArgumentException.class)
    public void addingStartChangeWithIncorrectNumberThrows() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setStartChange(MethodBuilder.parse(NumberOfBells.BELLS_5, "54123"));

    }
}