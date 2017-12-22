package org.ringingmaster.engine.parsernew.group;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.assignparse.AssignParseType;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.GROUP_CLOSE;
import static org.ringingmaster.engine.parser.ParseType.GROUP_OPEN;
import static org.ringingmaster.engine.parsernew.AssertParse.assertParse;
import static org.ringingmaster.engine.parsernew.AssertParse.invalid;
import static org.ringingmaster.engine.parsernew.AssertParse.unparsed;
import static org.ringingmaster.engine.parsernew.AssertParse.valid;
import static org.ringingmaster.engine.touch.newcontainer.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.newcontainer.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

public class GroupLogicTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

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

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertEquals(3, result.allTouchCells().getRowSize());
        assertEquals(2, result.allTouchCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allTouchCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allTouchCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allTouchCells().get(1,1).getCharacters());
    }

    @Test
    public void parsesNoContentPairOfGroupInSingleCell() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "()");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.allTouchCells().get(0,0), valid(1, GROUP_OPEN), valid(1, GROUP_CLOSE));
    }

    @Test
    public void groupInSingleCellInWrongOrderInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, ")(");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.allTouchCells().get(0,0), invalid(1, GROUP_CLOSE), invalid(1, GROUP_OPEN));
    }

    @Test
    public void groupOnMultiLineCellInWrongOrderInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, ")");
        touch.addCharacters(TOUCH_TABLE, 1,0, "(");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);
        assertParse(result.allTouchCells().get(0,0), invalid(1, GROUP_CLOSE));
        assertParse(result.allTouchCells().get(1,0), invalid(1, GROUP_OPEN));
    }

    @Test
    public void nestedGroupInSingleCellIsValid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "(())");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.allTouchCells().get(0,0), valid(1, GROUP_OPEN), valid(1, GROUP_OPEN), valid(1, GROUP_CLOSE), valid(1, GROUP_CLOSE));
    }

    @Test
    public void nestedGroupOnMultiLineIsValid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "(");
        touch.addCharacters(TOUCH_TABLE, 0,1, "(");
        touch.addCharacters(TOUCH_TABLE, 1,0, "))");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.allTouchCells().get(0,0), valid(1, GROUP_OPEN));
        assertParse(result.allTouchCells().get(0,1), valid(1, GROUP_OPEN));
        assertParse(result.allTouchCells().get(1,0), valid(1, GROUP_CLOSE), valid(1, GROUP_CLOSE));
    }


    @Test
    public void additionalOpeningGroupInSingleCellIsInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "(()");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.allTouchCells().get(0,0), invalid(1, GROUP_OPEN), valid(1, GROUP_OPEN), valid(1, GROUP_CLOSE));
    }

    @Test
    public void additionalOpeningGroupInMultiCellIsInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "(");
        touch.addCharacters(TOUCH_TABLE, 0,1, "(");
        touch.addCharacters(TOUCH_TABLE, 1,0, ")");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.allTouchCells().get(0,0), invalid(1, GROUP_OPEN));
        assertParse(result.allTouchCells().get(0,1), valid(1, GROUP_OPEN));
        assertParse(result.allTouchCells().get(1,0), valid(1, GROUP_CLOSE));
    }


    @Test
    public void nestedGroupWithSplicedAssignsInvalidityToCorrect() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.setSpliced(true);
        touch.addCharacters(TOUCH_TABLE, 0,0, "(");
        touch.addCharacters(TOUCH_TABLE, 0,1, "(");
        touch.addCharacters(TOUCH_TABLE, 1,0, "-");
        touch.addCharacters(TOUCH_TABLE, 1,1, ")");
        touch.addCharacters(TOUCH_TABLE, 2,0, ")");
        touch.addCharacters(TOUCH_TABLE, 2,1, ")");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.allTouchCells().get(0,0), valid(1, GROUP_OPEN));
        assertParse(result.allTouchCells().get(0,1), valid(1, GROUP_OPEN));
        assertParse(result.allTouchCells().get(1,0), valid(1, CALL));
        assertParse(result.allTouchCells().get(1,1), valid(1, GROUP_CLOSE));
        assertParse(result.allTouchCells().get(2,0), valid(1, GROUP_CLOSE));
        assertParse(result.allTouchCells().get(2,1), invalid(1, GROUP_CLOSE));
    }

    @Test
    public void groupsWithinCourseBasedInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.setTouchCheckingType(CheckingType.COURSE_BASED);
        touch.addCharacters(TOUCH_TABLE, 0,0, "(");
        touch.addCharacters(TOUCH_TABLE, 0,1, ")");
        touch.addCharacters(TOUCH_TABLE, 1,0, "-");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.allTouchCells().get(0,0), unparsed());
        assertParse(result.allTouchCells().get(0,1), unparsed());
    }

    @Test
    public void groupsWithinDefinitionValid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.addDefinition("DEF1", "(-)");
        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new GroupLogic().apply(parse);

        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0, DEFINITION_COLUMN), valid(GROUP_OPEN), valid(CALL ), valid(GROUP_CLOSE));
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