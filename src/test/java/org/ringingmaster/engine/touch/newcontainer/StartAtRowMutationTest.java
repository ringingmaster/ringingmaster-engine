package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.touch.container.Touch.START_AT_ROW_MAX;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class StartAtRowMutationTest {

    @Test
    public void hasCorrectDefault() {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(0, touch.get().getStartAtRow());
    }

    @Test
    public void changeIsReflected(){

        ObservableTouch touch = new ObservableTouch();

        touch.setStartAtRow(10);
        assertEquals(10, touch.get().getStartAtRow());

        touch.setStartAtRow(START_AT_ROW_MAX);
        assertEquals(START_AT_ROW_MAX, touch.get().getStartAtRow());

        touch.setStartAtRow(0);
        assertEquals(0, touch.get().getStartAtRow());
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingMinusOneOrLowerThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setStartAtRow(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void settingAboveMaxThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setStartAtRow(START_AT_ROW_MAX +1);
    }

}
