package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class AuthorMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertEquals("", composition.get().getAuthor());
    }

    @Test
    public void changeIsReflected() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setAuthor("TEST");

        assertEquals("TEST", composition.get().getAuthor());
    }
}
