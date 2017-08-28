package org.ringingmaster.engine.touch.newcontainer;

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

        ObservableTouch observableTouch = new ObservableTouch();

        assertEquals("", observableTouch.get().getTitle());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch observableTouch = new ObservableTouch();
        observableTouch.setTitle("TEST");

        assertEquals("TEST", observableTouch.get().getTitle());
    }
}
