package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class CompositionTypeMutationTest {

    @Test
    public void hasCorrectDefault() throws Exception {

        MutableComposition composition = new MutableComposition();

        assertEquals(CompositionType.COURSE_BASED, composition.get().getCompositionType());
    }

    @Test
    public void changeIsReflected() throws Exception {

        MutableComposition composition = new MutableComposition();
        composition.setCompositionType(CompositionType.LEAD_BASED);

        assertEquals(CompositionType.LEAD_BASED, composition.get().getCompositionType());
    }
}
