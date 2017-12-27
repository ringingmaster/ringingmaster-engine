package org.ringingmaster.engine.touch.container;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class StartNotationMutationTest {

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
    public void hasCorrectDefault() {

        ObservableTouch touch = new ObservableTouch();

        assertEquals(Optional.empty(), touch.get().getStartNotation());
    }

    @Test
    public void changeIsReflected(){

        ObservableTouch touch = new ObservableTouch();

        touch.setStartNotation(METHOD_A_6_BELL);
        assertEquals(Optional.of(METHOD_A_6_BELL), touch.get().getStartNotation());

        touch.setStartNotation(METHOD_B_6_BELL);
        assertEquals(Optional.of(METHOD_B_6_BELL), touch.get().getStartNotation());

        touch.removeStartNotation();
        assertEquals(Optional.empty(), touch.get().getStartNotation());

    }


}
