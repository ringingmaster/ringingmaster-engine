package org.ringingmaster.engine.parser.definition;

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
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

public class DefinitionInSplicedOrMainTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new DefinitionInSplicedOrMain().apply(parse);

        assertEquals(0, result.allTouchCells().getRowSize());
        assertEquals(0, result.allTouchCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor());
        touch.setCheckingType(COURSE_BASED);

        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL_POSITION");
        touch.addCharacters(TOUCH_TABLE, 1,0, "MAIN_BODY");
        touch.addCharacters(TOUCH_TABLE, 1,1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 2,0, "CALL");// To force the Parse to be replaced
        touch.addCharacters(TOUCH_TABLE, 2,1, "CALL");// To force the Parse to be replaced

        Parse result = new AssignParseType()
                .andThen(new DefinitionInSplicedOrMain())
                .apply(touch.get());

        assertEquals(3, result.allTouchCells().getRowSize());
        assertEquals(2, result.allTouchCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allTouchCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allTouchCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allTouchCells().get(1,1).getCharacters());
    }

    @Test
    public void differentDefinitionsValidInMainAndSpliced() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL");
        touch.addCharacters(TOUCH_TABLE, 0,1, "SPLICE");

        Parse result = new AssignParseType()
                .andThen(new DefinitionInSplicedOrMain())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allTouchCells().get(0,1), valid(6, DEFINITION));
    }

    @Test
    public void usingSameDefinitionInMainAndSplicedSetsBothInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL");
        touch.addCharacters(TOUCH_TABLE, 1,0, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 0,1, "CALL");

        Parse result = new AssignParseType()
                .andThen(new DefinitionInSplicedOrMain())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), invalid(4, DEFINITION));
        assertParse(result.allTouchCells().get(1,0), valid(6, DEFINITION));
        assertParse(result.allTouchCells().get(0,1), invalid(4, DEFINITION));
    }

    @Test
    public void usingSameDefinitionInEitherMainOrSplicedIsValid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL");
        touch.addCharacters(TOUCH_TABLE, 1,0, "CALL");
        touch.addCharacters(TOUCH_TABLE, 0,1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 1,1, "SPLICE");

        Parse result = new AssignParseType()
                .andThen(new DefinitionInSplicedOrMain())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allTouchCells().get(1,0), valid(4, DEFINITION));
        assertParse(result.allTouchCells().get(0,1), valid(6, DEFINITION));
        assertParse(result.allTouchCells().get(1,1), valid(6, DEFINITION));
    }

    @Test
    public void embeddedDefinitionInMainUsedInSplicedInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addDefinition("IN_MAIN", "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL");
        touch.addCharacters(TOUCH_TABLE, 0,1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 1,0, "IN_MAIN");

        Parse result = new AssignParseType()
                .andThen(new DefinitionInSplicedOrMain())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allTouchCells().get(0,1), invalid(6, DEFINITION));
        assertParse(result.findDefinitionByShorthand("IN_MAIN").get().get(0, DEFINITION_COLUMN), invalid(6, DEFINITION));
    }

    @Test
    public void embeddedDefinitionInMainTransitivelyUsedInSplicedInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addDefinition("IN_MAIN_1", "IN_MAIN_2");
        touch.addDefinition("IN_MAIN_2", "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL");
        touch.addCharacters(TOUCH_TABLE, 0,1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 1,0, "IN_MAIN_1");

        Parse result = new AssignParseType()
                .andThen(new DefinitionInSplicedOrMain())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allTouchCells().get(0,1), invalid(6, DEFINITION));
        assertParse(result.findDefinitionByShorthand("IN_MAIN_2").get().get(0, DEFINITION_COLUMN), invalid(6, DEFINITION));
    }

    @Test
    public void embeddedDefinitionInSplicedUsedInMainInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addDefinition("IN_SPICE", "CALL");
        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL");
        touch.addCharacters(TOUCH_TABLE, 0,1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 1,1, "IN_SPICE");

        Parse result = new AssignParseType()
                .andThen(new DefinitionInSplicedOrMain())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0,0), invalid(4, DEFINITION));
        assertParse(result.allTouchCells().get(0,1), valid(6, DEFINITION));
        assertParse(result.findDefinitionByShorthand("IN_SPICE").get().get(0, DEFINITION_COLUMN), invalid(4, DEFINITION));
    }

    @Test
    public void embeddedDefinitionInSplicedTransitivelyUsedInMainInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addDefinition("IN_SPICE_1", "IN_SPICE_2");
        touch.addDefinition("IN_SPICE_2", "CALL");
        touch.addCharacters(TOUCH_TABLE, 0, 0, "CALL");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 1, 1, "IN_SPICE_1");

        Parse result = new AssignParseType()
                .andThen(new DefinitionInSplicedOrMain())
                .apply(touch.get());

        assertParse(result.allTouchCells().get(0, 0), invalid(4, DEFINITION));
        assertParse(result.allTouchCells().get(0, 1), valid(6, DEFINITION));
        assertParse(result.findDefinitionByShorthand("IN_SPICE_2").get().get(0, DEFINITION_COLUMN), invalid(4, DEFINITION));
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
        touch.setCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(true);
        touch.addDefinition("CALL", "-1-");
        touch.addDefinition("SPLICE", "P");
        return touch;
    }

}