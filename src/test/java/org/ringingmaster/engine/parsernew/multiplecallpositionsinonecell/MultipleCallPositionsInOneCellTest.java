package org.ringingmaster.engine.parsernew.multiplecallpositionsinonecell;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.assignparsetype.AssignParseType;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.parsernew.AssertParse.assertParse;
import static org.ringingmaster.engine.parsernew.AssertParse.invalid;
import static org.ringingmaster.engine.parsernew.AssertParse.unparsed;
import static org.ringingmaster.engine.parsernew.AssertParse.valid;

public class MultipleCallPositionsInOneCellTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor());
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new MultipleCallPositionsInOneCell().parse(parse);

        assertEquals(0, result.allCells().getRowSize());
        assertEquals(0, result.allCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor());
        touch.setSpliced(true);

        touch.addCharacters(0,0, "CALL_POSITION");
        touch.addCharacters(1,0, "MAIN_BODY");
        touch.addCharacters(1,1, "SPLICE");

        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new MultipleCallPositionsInOneCell().parse(parse);

        assertEquals(2, result.allCells().getRowSize());
        assertEquals(2, result.allCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCells().get(1,1).getCharacters());
    }

    @Test
    public void parsingGoodCallPositionTakesNoAction() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(0,0, "W");
        touch.addCharacters(0,1, "H");
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new MultipleCallPositionsInOneCell().parse(parse);

        assertParse(result.allCells().get(0,0), valid(CALLING_POSITION));
        assertParse(result.allCells().get(0,0), valid(CALLING_POSITION));
    }

    @Test
    public void parsingDuplicateMarksSeconsAsInvalid() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(0,0, "WH");
        touch.addCharacters(0,1, "-HW");
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new MultipleCallPositionsInOneCell().parse(parse);

        assertParse(result.allCells().get(0,0), valid(CALLING_POSITION), invalid(CALLING_POSITION));
        assertParse(result.allCells().get(0,1), unparsed() ,valid(CALLING_POSITION), invalid(CALLING_POSITION));
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

    private ObservableTouch buildAndParseSingleCellTouch(NotationBody notationBody) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        touch.addNotation(notationBody);
        touch.setTouchCheckingType(CheckingType.COURSE_BASED);
        touch.setSpliced(false);
        return touch;
    }

}