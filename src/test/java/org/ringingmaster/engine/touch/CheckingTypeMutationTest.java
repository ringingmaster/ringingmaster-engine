package org.ringingmaster.engine.touch;

import org.junit.Test;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CheckingTypeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(CheckingType.COURSE_BASED, touch.get().getCheckingType());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setTouchCheckingType(CheckingType.LEAD_BASED);

        assertEquals(CheckingType.LEAD_BASED, touch.get().getCheckingType());
    }
}
