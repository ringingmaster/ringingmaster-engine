package org.ringingmaster.engine.parser.brace;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparse.AssignParseType;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

public class VarianceLogicTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new VarianceLogic())
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
                .andThen(new VarianceLogic())
                .apply(touch.get());

        assertEquals(3, result.allTouchCells().getRowSize());
        assertEquals(2, result.allTouchCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allTouchCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allTouchCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allTouchCells().get(1,1).getCharacters());
    }

    @Test
    public void parsesNoContentPairOfVarianceInSingleCell() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "[]");

        Parse result = new AssignParseType()
                .andThen(new VarianceLogic())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), valid(1, VARIANCE_OPEN), valid(1, VARIANCE_CLOSE));
    }

    @Test
    public void varianceInSingleCellInWrongOrderInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "][");

        Parse result = new AssignParseType()
                .andThen(new VarianceLogic())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), invalid(1, VARIANCE_CLOSE), invalid(1, VARIANCE_OPEN));
    }

    @Test
    public void varianceOnMultiLineCellInWrongOrderInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "]");
        touch.addCharacters(TOUCH_TABLE, 1,0, "[");

        Parse result = new AssignParseType()
                .andThen(new VarianceLogic())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), invalid(1, VARIANCE_CLOSE));
        assertParse(result.allTouchCells().get(1,0), invalid(1, VARIANCE_OPEN));
    }

    @Test
    public void variancesWithinCourseBasedInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.setTouchCheckingType(CheckingType.COURSE_BASED);
        touch.addCharacters(TOUCH_TABLE, 0,0, "[");
        touch.addCharacters(TOUCH_TABLE, 0,1, "]");
        touch.addCharacters(TOUCH_TABLE, 1,0, "-");

        Parse result = new AssignParseType()
                .andThen(new VarianceLogic())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), unparsed());
        assertParse(result.allTouchCells().get(0,1), unparsed());
    }

    @Test
    public void varianceWithinDefinitionValid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addDefinition("DEF1", "[-]");

        Parse result = new AssignParseType()
                .andThen(new VarianceLogic())
                .apply(touch.get());

        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0, DEFINITION_COLUMN), valid(VARIANCE_OPEN), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void nestingVarianceinvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "[[-]]");

        Parse result = new AssignParseType()
                .andThen(new VarianceLogic())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0),
                valid(1, VARIANCE_OPEN), invalid(1, VARIANCE_OPEN),
                valid(1, CALL),
                valid(1, VARIANCE_CLOSE), invalid(1, VARIANCE_CLOSE)
        );
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
        touch.setTouchCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(false);
        return touch;
    }
}