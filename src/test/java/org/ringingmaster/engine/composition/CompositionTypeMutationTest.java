package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class CompositionTypeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(CompositionType.COURSE_BASED, composition.get().getCompositionType());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setCheckingType(CompositionType.LEAD_BASED);

        assertEquals(CompositionType.LEAD_BASED, composition.get().getCompositionType());
    }
}
