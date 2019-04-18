package org.ringingmaster.engine.touch;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NumberOfBellsMutationTest {

    public static final NotationBody METHOD_A_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD A", "12");
    public static final NotationBody METHOD_A_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD A", "12");
    public static final NotationBody METHOD_B_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD B", "78");

    private static NotationBody buildNotation(NumberOfBells bells, String name, String notation1) {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(bells)
                .setName(name)
                .setUnfoldedNotationShorthand(notation1)
                .build();
    }


    @Test
    public void hasCorrectDefault() throws Exception {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(NumberOfBells.BELLS_6, touch.get().getNumberOfBells());
    }

    @Test
    public void changeIsReflectedInNonOptionalFields() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(NumberOfBells.BELLS_20);

        assertEquals(NumberOfBells.BELLS_20, touch.get().getNumberOfBells());
        assertEquals(NumberOfBells.BELLS_20, touch.get().getStartChange().getNumberOfBells());
    }

    @Test
    public void whenActiveNotationIsNoLongerValidBestAlternativeIsSelected() throws Exception {

        //TODO - many tests
        fail();
    }

    @Test
    public void whenTerminationChangeExistsChangeIsReflected() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(false);
        touch.setNumberOfBells(NumberOfBells.BELLS_8);

        touch.setTerminationChange(MethodBuilder.parse(NumberOfBells.BELLS_8, "87654321"));
        assertEquals("87654321", touch.get().getTerminationChange().get().getDisplayString(false));

        touch.setNumberOfBells(NumberOfBells.BELLS_6);
        assertEquals("654321", touch.get().getTerminationChange().get().getDisplayString(false));
    }

    @Test
    public void whenStartNotationExistsAndConversionPossibleStartNotationChanged() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(false);
        touch.setNumberOfBells(NumberOfBells.BELLS_8);

        touch.setStartNotation(METHOD_A_8_BELL);
        assertEquals("12", touch.get().getStartNotation().get().getNotationDisplayString(false));

        touch.setNumberOfBells(NumberOfBells.BELLS_6);
        assertEquals("12", touch.get().getStartNotation().get().getNotationDisplayString(false));
        assertEquals(NumberOfBells.BELLS_6, touch.get().getStartNotation().get().getNumberOfWorkingBells());
    }

    @Test
    public void whenStartNotationExistsAndConversionNotPossibleStartNotationRemoved() throws Exception {

        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(false);
        touch.setNumberOfBells(NumberOfBells.BELLS_8);

        touch.setStartNotation(METHOD_B_8_BELL);
        assertEquals("78", touch.get().getStartNotation().get().getNotationDisplayString(false));

        touch.setNumberOfBells(NumberOfBells.BELLS_6);
        assertFalse(touch.get().getStartNotation().isPresent());
    }

    @Test
    public void settingNumberOfBellsResetsActiveNotation() {
        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(false);
        touch.setNumberOfBells(NumberOfBells.BELLS_8);
        touch.addNotation(METHOD_A_8_BELL);
        touch.addNotation(METHOD_A_6_BELL);

        assertEquals("METHOD A Major", touch.get().getNonSplicedActiveNotation().get().getNameIncludingNumberOfBells());

        touch.setNumberOfBells(NumberOfBells.BELLS_6);
        assertEquals("METHOD A Minor", touch.get().getNonSplicedActiveNotation().get().getNameIncludingNumberOfBells());
    }

}
