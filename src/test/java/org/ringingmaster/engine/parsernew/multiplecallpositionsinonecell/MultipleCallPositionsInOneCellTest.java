package org.ringingmaster.engine.parsernew.multiplecallpositionsinonecell;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.ParseType;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void parsingGoodCallPositionTakesNoAction() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(0,0, "W");
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new MultipleCallPositionsInOneCell().parse(parse);


        ParsedCell parsedCell = result.allCells().get(0, 0);
        assertEquals(1, parsedCell.getElementSize());
        assertEquals("W", parsedCell.getElement(0).getCharacter());
        assertEquals(ParseType.CALLING_POSITION, parsedCell.getSectionAtElementIndex(0).get().getParseType());
        assertTrue(parsedCell.getGroupAtElementIndex(0).get().isValid());
    }

    @Test
    public void parsingDuplicateMarksSeconsAsInvalid() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(0,0, "WH");
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new MultipleCallPositionsInOneCell().parse(parse);


        ParsedCell parsedCell = result.allCells().get(0, 0);
        assertEquals(2, parsedCell.getElementSize());
        assertEquals("W", parsedCell.getElement(0).getCharacter());
        assertEquals("H", parsedCell.getElement(1).getCharacter());
        assertEquals(ParseType.CALLING_POSITION, parsedCell.getSectionAtElementIndex(0).get().getParseType());
        assertEquals(ParseType.CALLING_POSITION, parsedCell.getSectionAtElementIndex(1).get().getParseType());
        assertTrue(parsedCell.getGroupAtElementIndex(0).get().isValid());
        assertFalse(parsedCell.getGroupAtElementIndex(1).get().isValid());
    }

    //TODO more tests

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