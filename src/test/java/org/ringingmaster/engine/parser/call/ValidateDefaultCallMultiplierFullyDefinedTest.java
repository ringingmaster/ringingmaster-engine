package org.ringingmaster.engine.parser.call;

import com.google.common.collect.Iterables;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ValidateDefaultCallMultiplierFullyDefinedTest {

    @Test
    public void defaultCallNotDefinedInAllMethodsInSplicedSetsInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "2");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "DUMMY");
        touch.addNotation(buildLittleBobMinorWithNoDefaultCall());
        touch.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new ValidateDefaultCallMultiplierFullyDefined())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), invalid(DEFAULT_CALL_MULTIPLIER));
    }

    @Test
    public void defaultCallNotDefinedInChosenMethodSetsInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "2");
        touch.removeNotation(Iterables.getOnlyElement(touch.get().getAllNotations()));

        Parse parse = new AssignParseType()
                .andThen(new ValidateDefaultCallMultiplierFullyDefined())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), invalid( DEFAULT_CALL_MULTIPLIER, "No default call defined"));
    }


    private NotationBody buildPlainBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("x16x16x16", "12")
                .addCall("Bob", "-", "14", true)
                .addCall("Single", "s", "1234", false)
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("p")
                .build();
    }

    private NotationBody buildLittleBobMinorWithNoDefaultCall() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("x16x14", "12")
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("l")
                .build();
    }

    private ObservableTouch buildSingleCellTouch(NotationBody notationBody, String characters) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        if (characters != null) {
            touch.addCharacters(TOUCH_TABLE, 0, 0, characters);
        }
        touch.addNotation(notationBody);
        touch.setCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(false);
        touch.addDefinition("def1", "-P");
        touch.addDefinition("def2", "2");
        return touch;
    }


}