package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class TitleMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertEquals(CompositionBuilder.DEFAULT_TITLE, composition.get().getTitle());
    }

    @Test
    public void changeIsReflected() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setTitle("TEST");

        assertEquals("TEST", composition.get().getTitle());
    }
}
