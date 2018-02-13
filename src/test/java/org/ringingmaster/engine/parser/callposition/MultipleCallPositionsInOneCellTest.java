package org.ringingmaster.engine.parser.callposition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;

public class MultipleCallPositionsInOneCellTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        Parse result = new AssignParseType()
                .andThen(new MultipleCallPositionsInOneCell())
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

        Parse result = new AssignParseType()
                .andThen(new MultipleCallPositionsInOneCell())
                .apply(touch.get());

        assertEquals(2, result.allTouchCells().getRowSize());
        assertEquals(2, result.allTouchCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allTouchCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allTouchCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allTouchCells().get(1,1).getCharacters());
    }

    @Test
    public void parsingGoodCallPositionTakesNoAction() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "W");
        touch.addCharacters(TOUCH_TABLE, 0,1, "H");

        Parse result = new AssignParseType()
                .andThen(new MultipleCallPositionsInOneCell())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), valid(CALLING_POSITION));
        assertParse(result.allTouchCells().get(0,0), valid(CALLING_POSITION));
    }

    @Test
    public void parsingDuplicateMarksSeconsAsInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "WH");
        touch.addCharacters(TOUCH_TABLE, 0,1, "-HW");

        Parse result = new AssignParseType()
                .andThen(new MultipleCallPositionsInOneCell())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), valid(CALLING_POSITION), invalid(CALLING_POSITION));
        assertParse(result.allTouchCells().get(0,1), unparsed() ,valid(CALLING_POSITION), invalid(CALLING_POSITION));
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

    private ObservableTouch buildSingleCellTouch(NotationBody notationBody) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        touch.addNotation(notationBody);
        touch.setTouchCheckingType(CheckingType.COURSE_BASED);
        touch.setSpliced(false);
        return touch;
    }

}