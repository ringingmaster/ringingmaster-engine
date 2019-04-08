package org.ringingmaster.engine.parser.brace;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;

public class ValidateMultiplierGroupAndVarianceDontOverlapTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertEquals(0, result.allTouchCells().getRowSize());
        assertEquals(0, result.allTouchCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.setSpliced(true);

        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL_POSITION");
        touch.addCharacters(TOUCH_TABLE, 1,0, "MAIN_BODY");
        touch.addCharacters(TOUCH_TABLE, 1,1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 2,0, "abc");// To force the Parse to be replaced
        touch.addCharacters(TOUCH_TABLE, 2,1, "abc");// To force the Parse to be replaced

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertEquals(3, result.allTouchCells().getRowSize());
        assertEquals(2, result.allTouchCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allTouchCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allTouchCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allTouchCells().get(1,1).getCharacters());
    }



    @Test
    public void VarianceEnclosedByGroupValid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "([-o])");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0),
                valid( MULTIPLIER_GROUP_OPEN), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid( VARIANCE_CLOSE), valid( MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void GroupEnclosedByVarianceValid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "[-o()]");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0),
                valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_CLOSE), valid( VARIANCE_CLOSE));
    }

    @Test
    public void GroupOverlappingVarianceInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "([-e)]");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0),
                invalid( MULTIPLIER_GROUP_OPEN), invalid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), invalid( MULTIPLIER_GROUP_CLOSE), invalid( VARIANCE_CLOSE));
    }

    @Test
    public void VarianceOverlappingGroupInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "[-e(])");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0),
                invalid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), invalid( MULTIPLIER_GROUP_OPEN), invalid( VARIANCE_CLOSE), invalid( MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void MultiVarianceOverlappingGroupInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "[-o(][-e)]");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0),
                valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), invalid( MULTIPLIER_GROUP_OPEN), invalid( VARIANCE_CLOSE), invalid("Variances and Groups can't overlap", section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), invalid( MULTIPLIER_GROUP_CLOSE), valid( VARIANCE_CLOSE));
    }

    @Test
    public void ignoresAlreadyInvalidBrace() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "(])"); //this sequence caused crash

        Parse result = new AssignParseType()
//                this line will pre-invalidate the closing variance brace.
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0),
                valid( MULTIPLIER_GROUP_OPEN), invalid( VARIANCE_CLOSE), valid(MULTIPLIER_GROUP_CLOSE));
    }


    @Test
    public void closingBraceBeforeOpeningBraceDoesNotThrow() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "(])]"); //this sequence caused crash

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0),
                valid(MULTIPLIER_GROUP_OPEN), invalid(VARIANCE_CLOSE), valid(MULTIPLIER_GROUP_CLOSE), invalid(VARIANCE_CLOSE));
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

    private ObservableTouch buildSingleCellTouch(NotationBody... notations) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notations[0].getNumberOfWorkingBells());
        Arrays.stream(notations).forEach(touch::addNotation);
        touch.setCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(false);
        return touch;
    }
}