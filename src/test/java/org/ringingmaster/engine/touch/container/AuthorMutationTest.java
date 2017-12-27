package org.ringingmaster.engine.touch.container;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class AuthorMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals("", touch.get().getAuthor());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setAuthor("TEST");

        assertEquals("TEST", touch.get().getAuthor());
    }
}
