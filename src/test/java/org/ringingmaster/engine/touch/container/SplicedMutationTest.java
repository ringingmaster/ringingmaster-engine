package org.ringingmaster.engine.touch.container;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class SplicedMutationTest {

    public static final NotationBody METHOD_A_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD A", "12");
    public static final NotationBody METHOD_B_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD B", "14");

    private static NotationBody buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch observableTouch = new ObservableTouch();

        assertEquals(false, observableTouch.get().isSpliced());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.addNotation(METHOD_A_6_BELL);
        touch.setSpliced(true);

        assertEquals(true, touch.get().isSpliced());
    }

    @Test
    public void settingSplicedIneffectiveWhenNoMethods() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(true);

        assertEquals(false, touch.get().isSpliced());
    }

    @Test
    public void settingSplicedClearsActiveNotation() {
        ObservableTouch touch = new ObservableTouch();
        touch.addNotation(METHOD_A_6_BELL);
        touch.addNotation(METHOD_B_6_BELL);

        assertTrue(touch.get().getNonSplicedActiveNotation().isPresent());
        touch.setSpliced(true);
        assertFalse(touch.get().getNonSplicedActiveNotation().isPresent());
    }

    @Test
    public void unsettingSplicedSetsFirstActiveNotation() {
        ObservableTouch touch = new ObservableTouch();
        touch.addNotation(METHOD_A_6_BELL);
        touch.addNotation(METHOD_B_6_BELL);

        touch.setSpliced(true);
        assertFalse(touch.get().getNonSplicedActiveNotation().isPresent());
        touch.setSpliced(false);
        assertTrue(touch.get().getNonSplicedActiveNotation().isPresent());
    }

}