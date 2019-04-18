package org.ringingmaster.engine.composition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TitleMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(CompositionBuilder.DEFAULT_TITLE, composition.get().getTitle());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setTitle("TEST");

        assertEquals("TEST", composition.get().getTitle());
    }
}
