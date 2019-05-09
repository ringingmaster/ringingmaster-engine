package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.MethodBuilder;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class StartChangeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertEquals("123456", composition.get().getStartChange().getDisplayString(false));
    }

    @Test
    public void changeIsReflected() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setStartChange(MethodBuilder.parse(NumberOfBells.BELLS_6, "654123"));

        assertEquals("654123", composition.get().getStartChange().getDisplayString(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingStartChangeWithIncorrectNumberThrows() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setStartChange(MethodBuilder.parse(NumberOfBells.BELLS_5, "54123"));

    }

    @Test(expected = NullPointerException.class)
    public void addingNullStartChangeWith() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setStartChange(null);
    }
}