package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class PlainLeadTokenMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition composition = new ObservableComposition();

        assertEquals("p", composition.get().getPlainLeadToken());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setPlainLeadToken("TEST");

        assertEquals("TEST", composition.get().getPlainLeadToken());
    }
}
