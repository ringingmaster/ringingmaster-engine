package org.ringingmaster.engine.composition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NumberOfBellsMutationTest {

    public static final Notation METHOD_A_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD A", "12");
    public static final Notation METHOD_A_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD A", "12");
    public static final Notation METHOD_B_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD B", "78");

    private static Notation buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }


    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableComposition composition = new ObservableComposition();

        assertEquals(NumberOfBells.BELLS_6, composition.get().getNumberOfBells());
    }

    @Test
    public void changeIsReflectedInNonOptionalFields() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(NumberOfBells.BELLS_20);

        assertEquals(NumberOfBells.BELLS_20, composition.get().getNumberOfBells());
        assertEquals(NumberOfBells.BELLS_20, composition.get().getStartChange().getNumberOfBells());
    }

    @Test
    public void whenActiveNotationIsNoLongerValidBestAlternativeIsSelected() throws Exception {

        //TODO - many tests
        fail();
    }

    @Test
    public void whenTerminationChangeExistsChangeIsReflected() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setSpliced(false);
        composition.setNumberOfBells(NumberOfBells.BELLS_8);

        composition.setTerminationChange(MethodBuilder.parse(NumberOfBells.BELLS_8, "87654321"));
        assertEquals("87654321", composition.get().getTerminationChange().get().getDisplayString(false));

        composition.setNumberOfBells(NumberOfBells.BELLS_6);
        assertEquals("654321", composition.get().getTerminationChange().get().getDisplayString(false));
    }

    @Test
    public void whenStartNotationExistsAndConversionPossibleStartNotationChanged() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setSpliced(false);
        composition.setNumberOfBells(NumberOfBells.BELLS_8);

        composition.setStartNotation(METHOD_A_8_BELL);
        assertEquals("12", composition.get().getStartNotation().get().getNotationDisplayString(false));

        composition.setNumberOfBells(NumberOfBells.BELLS_6);
        assertEquals("12", composition.get().getStartNotation().get().getNotationDisplayString(false));
        assertEquals(NumberOfBells.BELLS_6, composition.get().getStartNotation().get().getNumberOfWorkingBells());
    }

    @Test
    public void whenStartNotationExistsAndConversionNotPossibleStartNotationRemoved() throws Exception {

        ObservableComposition composition = new ObservableComposition();
        composition.setSpliced(false);
        composition.setNumberOfBells(NumberOfBells.BELLS_8);

        composition.setStartNotation(METHOD_B_8_BELL);
        assertEquals("78", composition.get().getStartNotation().get().getNotationDisplayString(false));

        composition.setNumberOfBells(NumberOfBells.BELLS_6);
        assertFalse(composition.get().getStartNotation().isPresent());
    }

    @Test
    public void settingNumberOfBellsResetsActiveNotation() {
        ObservableComposition composition = new ObservableComposition();
        composition.setSpliced(false);
        composition.setNumberOfBells(NumberOfBells.BELLS_8);
        composition.addNotation(METHOD_A_8_BELL);
        composition.addNotation(METHOD_A_6_BELL);

        assertEquals("METHOD A Major", composition.get().getNonSplicedActiveNotation().get().getNameIncludingNumberOfBells());

        composition.setNumberOfBells(NumberOfBells.BELLS_6);
        assertEquals("METHOD A Minor", composition.get().getNonSplicedActiveNotation().get().getNameIncludingNumberOfBells());
    }

}
