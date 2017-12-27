package org.ringingmaster.engine.touch.container;

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

        ObservableTouch touch = new ObservableTouch();

        assertEquals(Stroke.BACKSTROKE, touch.get().getStartStroke());
    }

    @Test
    public void changeIsReflected(){

        ObservableTouch touch = new ObservableTouch();

        touch.setStartStroke(Stroke.HANDSTROKE);
        assertEquals(Stroke.HANDSTROKE, touch.get().getStartStroke());
    }
}
