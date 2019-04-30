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

        MutableComposition composition = new MutableComposition();

        assertEquals(Stroke.BACKSTROKE, composition.get().getStartStroke());
    }

    @Test
    public void changeIsReflected(){

        MutableComposition composition = new MutableComposition();

        composition.setStartStroke(Stroke.HANDSTROKE);
        assertEquals(Stroke.HANDSTROKE, composition.get().getStartStroke());
    }
}
