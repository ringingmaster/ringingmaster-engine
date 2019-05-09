package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class PlainLeadTokenMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertEquals("p", composition.get().getPlainLeadToken());
    }

    @Test
    public void changeIsReflected() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setPlainLeadToken("TEST");

        assertEquals("TEST", composition.get().getPlainLeadToken());
    }
}
