package org.ringingmaster.engine.touch;

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

        ObservableTouch touch = new ObservableTouch();

        assertEquals("p", touch.get().getPlainLeadToken());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setPlainLeadToken("TEST");

        assertEquals("TEST", touch.get().getPlainLeadToken());
    }
}
