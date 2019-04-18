package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.method.Stroke;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class StartStrokeMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(Stroke.BACKSTROKE, composition.get().getStartStroke());
    }

    @Test
    public void changeIsReflected(){

        ObservableComposition composition = new ObservableComposition();

        composition.setStartStroke(Stroke.HANDSTROKE);
        assertEquals(Stroke.HANDSTROKE, composition.get().getStartStroke());
    }
}
