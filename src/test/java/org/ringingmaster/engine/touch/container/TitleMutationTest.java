package org.ringingmaster.engine.touch.container;

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

        ObservableTouch touch = new ObservableTouch();

        assertEquals("", touch.get().getTitle());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTitle("TEST");

        assertEquals("TEST", touch.get().getTitle());
    }
}
