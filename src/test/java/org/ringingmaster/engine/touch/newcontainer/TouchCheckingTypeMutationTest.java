package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Test;
import org.ringingmaster.engine.touch.container.TouchCheckingType;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class TouchCheckingTypeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(TouchCheckingType.COURSE_BASED, touch.get().getTouchCheckingType());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTouchCheckingType(TouchCheckingType.LEAD_BASED);

        assertEquals(TouchCheckingType.LEAD_BASED, touch.get().getTouchCheckingType());
    }
}
