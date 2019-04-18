package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.composition.checkingtype.CheckingType;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CheckingTypeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(CheckingType.COURSE_BASED, composition.get().getCheckingType());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setCheckingType(CheckingType.LEAD_BASED);

        assertEquals(CheckingType.LEAD_BASED, composition.get().getCheckingType());
    }
}
