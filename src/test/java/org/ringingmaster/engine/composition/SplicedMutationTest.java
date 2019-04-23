package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class SplicedMutationTest {

    public static final Notation METHOD_A_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD A", "12");
    public static final Notation METHOD_B_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD B", "14");

    private static Notation buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }

    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition observableComposition = new ObservableComposition();

        assertEquals(false, observableComposition.get().isSpliced());
    }

    @Test
    public void changeIsReflected() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.addNotation(METHOD_A_6_BELL);
        composition.setSpliced(true);

        assertEquals(true, composition.get().isSpliced());
    }

    @Test
    public void settingSplicedIneffectiveWhenNoMethods() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setSpliced(true);

        assertEquals(false, composition.get().isSpliced());
    }

    @Test
    public void settingSplicedClearsActiveNotation() {
        ObservableComposition composition = new ObservableComposition();
        composition.addNotation(METHOD_A_6_BELL);
        composition.addNotation(METHOD_B_6_BELL);

        assertTrue(composition.get().getNonSplicedActiveNotation().isPresent());
        composition.setSpliced(true);
        assertFalse(composition.get().getNonSplicedActiveNotation().isPresent());
    }

    @Test
    public void unsettingSplicedSetsFirstActiveNotation() {
        ObservableComposition composition = new ObservableComposition();
        composition.addNotation(METHOD_A_6_BELL);
        composition.addNotation(METHOD_B_6_BELL);

        composition.setSpliced(true);
        assertFalse(composition.get().getNonSplicedActiveNotation().isPresent());
        composition.setSpliced(false);
        assertTrue(composition.get().getNonSplicedActiveNotation().isPresent());
    }

}