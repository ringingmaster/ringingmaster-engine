package org.ringingmaster.engine.touch.newcontainer;

import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NumberOfBellsMutationTest {

    public static final NotationBody METHOD_A_6_BELL = buildNotation(NumberOfBells.BELLS_6, "METHOD A", "12");
    public static final NotationBody METHOD_A_8_BELL = buildNotation(NumberOfBells.BELLS_8, "METHOD A", "12");

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

        //TODO
        fail();
    }

    @Test
    public void whenStartNotationExistsChangeIsReflected() throws Exception {

        //TODO
        fail();
    }
    @Test
    public void settingNumberOfBellsResetsActiveNotation() {
        ObservableTouch touch = new ObservableTouch();
        touch.setSpliced(false);
        touch.setNumberOfBells(NumberOfBells.BELLS_8);
        touch.addNotation(METHOD_A_8_BELL);
        touch.addNotation(METHOD_A_6_BELL);

        Assert.assertEquals("METHOD A Major", touch.get().getNonSplicedActiveNotation().get().getNameIncludingNumberOfBells());

        touch.setNumberOfBells(NumberOfBells.BELLS_6);
        Assert.assertEquals("METHOD A Minor", touch.get().getNonSplicedActiveNotation().get().getNameIncludingNumberOfBells());
    }

}
