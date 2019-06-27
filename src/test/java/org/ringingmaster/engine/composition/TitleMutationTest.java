package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class TitleMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertTrue(composition.get().getLoggingTag().startsWith(CompositionBuilder.DEFAULT_TITLE));
    }

    @Test
    public void changeIsReflected() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setTitle("TEST");

        assertTrue(composition.get().getLoggingTag().startsWith("TEST"));
    }
}
