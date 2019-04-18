package org.ringingmaster.engine.composition;

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

        ObservableComposition composition = new ObservableComposition();

        assertEquals(Optional.empty(), composition.get().getStartNotation());
    }

    @Test
    public void changeIsReflected(){

        ObservableComposition composition = new ObservableComposition();

        composition.setStartNotation(METHOD_A_6_BELL);
        assertEquals(Optional.of(METHOD_A_6_BELL), composition.get().getStartNotation());

        composition.setStartNotation(METHOD_B_6_BELL);
        assertEquals(Optional.of(METHOD_B_6_BELL), composition.get().getStartNotation());

        composition.removeStartNotation();
        assertEquals(Optional.empty(), composition.get().getStartNotation());

    }


}
