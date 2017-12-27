package org.ringingmaster.engine.touch.container;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.impl.MethodBuilder;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class StartChangeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals("123456", touch.get().getStartChange().getDisplayString(false));
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setStartChange(MethodBuilder.parse(NumberOfBells.BELLS_6, "654123"));

        assertEquals("654123", touch.get().getStartChange().getDisplayString(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingStartChangeWithIncorrectNumberThrows() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setStartChange(MethodBuilder.parse(NumberOfBells.BELLS_5, "54123"));

    }

    @Test(expected = NullPointerException.class)
    public void addingNullStartChangeWith() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setStartChange(null);
    }
}