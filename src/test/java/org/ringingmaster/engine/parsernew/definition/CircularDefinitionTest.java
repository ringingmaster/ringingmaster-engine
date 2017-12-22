package org.ringingmaster.engine.parsernew.definition;

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
import static org.ringingmaster.engine.parser.ParseType.DEFINITION;
import static org.ringingmaster.engine.parsernew.AssertParse.assertParse;
import static org.ringingmaster.engine.parsernew.AssertParse.invalid;
import static org.ringingmaster.engine.parsernew.AssertParse.valid;
import static org.ringingmaster.engine.touch.newcontainer.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.newcontainer.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

public class CircularDefinitionTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new DefinitionInSplicedOrMain().parse(parse);

        assertEquals(0, result.allTouchCells().getRowSize());
        assertEquals(0, result.allTouchCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.setTouchCheckingType(COURSE_BASED);

        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL_POSITION");
        touch.addCharacters(TOUCH_TABLE, 1,0, "MAIN_BODY");
        touch.addCharacters(TOUCH_TABLE, 1,1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 2,0, "CALL");// To force the Parse to be replaced
        touch.addCharacters(TOUCH_TABLE, 2,1, "CALL");// To force the Parse to be replaced

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new CircularDefinition().parse(parse);

        assertEquals(3, result.allTouchCells().getRowSize());
        assertEquals(2, result.allTouchCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allTouchCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allTouchCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allTouchCells().get(1,1).getCharacters());
    }

    @Test
    public void circularDependencyInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addDefinition("DEF_1", "DEF_2");
        touch.addDefinition("DEF_2", "DEF_3");
        touch.addDefinition("DEF_3", "DEF_1");
        touch.addCharacters(TOUCH_TABLE, 0,0, "DEF_1");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new CircularDefinition().parse(parse);

        assertParse(result.allTouchCells().get(0,0), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_1").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_2").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_3").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
    }

    @Test
    public void circularDependencyleavesAdditionalPathValid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addDefinition("DEF_1", "DEF_2DEF_3");
        touch.addDefinition("DEF_2", "DEF_1");
        touch.addDefinition("DEF_3", "-");
        touch.addCharacters(TOUCH_TABLE, 0,0, "DEF_1");

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new CircularDefinition().parse(parse);

        assertParse(result.allTouchCells().get(0,0), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_1").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION), valid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_2").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_3").get().get(0, DEFINITION_COLUMN), valid(CALL));
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
                .setSpliceIdentifier("P")
                .build();
    }

    private NotationBody buildLittleBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("x16x14", "12")
                .addCall("Bob", "-", "14", true)
                .addCall("Single", "s", "1234", false)
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("l")
                .build();
    }

    private ObservableTouch buildSingleCellTouch(NotationBody... notations) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notations[0].getNumberOfWorkingBells());
        Arrays.stream(notations).forEach(touch::addNotation);
        touch.setTouchCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(true);
        touch.addDefinition("CALL", "-1-");
        touch.addDefinition("SPLICE", "P");
        return touch;
    }

}