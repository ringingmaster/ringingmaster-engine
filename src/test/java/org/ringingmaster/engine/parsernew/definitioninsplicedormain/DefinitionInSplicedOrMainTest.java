package org.ringingmaster.engine.parsernew.definitioninsplicedormain;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parsernew.Parse;
import org.ringingmaster.engine.parsernew.assignparsetype.AssignParseType;
import org.ringingmaster.engine.touch.newcontainer.ObservableTouch;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.ParseType.DEFINITION;
import static org.ringingmaster.engine.parsernew.AssertParse.assertParse;
import static org.ringingmaster.engine.parsernew.AssertParse.invalid;
import static org.ringingmaster.engine.parsernew.AssertParse.valid;

public class DefinitionInSplicedOrMainTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor());
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new DefinitionInSplicedOrMain().parse(parse);

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
        touch.addCharacters(2,0, "abc");// To force the Parse to be replaced
        touch.addCharacters(2,1, "abc");// To force the Parse to be replaced

        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new DefinitionInSplicedOrMain().parse(parse);

        assertEquals(3, result.allCells().getRowSize());
        assertEquals(2, result.allCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCells().get(1,1).getCharacters());
    }

    @Test
    public void differentDefinitionsValidInMainAndSpliced() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addCharacters(0,0, "abc");
        touch.addCharacters(0,1, "xyz");
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new DefinitionInSplicedOrMain().parse(parse);

        assertParse(result.allCells().get(0,0), valid(3, DEFINITION));
        assertParse(result.allCells().get(0,1), valid(3, DEFINITION));
    }

    @Test
    public void usingSameDefinitionInMainAndSplicedSetsBothInvalid() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addCharacters(0,0, "abc");
        touch.addCharacters(1,0, "xyz");
        touch.addCharacters(0,1, "abc");
        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new DefinitionInSplicedOrMain().parse(parse);

        assertParse(result.allCells().get(0,0), invalid(3, DEFINITION));
        assertParse(result.allCells().get(0,1), invalid(3, DEFINITION));
        assertParse(result.allCells().get(1,0), valid(3, DEFINITION));
    }

    @Test
    public void usingSameDefinitionInEitherMainOrSplicedIsValid() {
        ObservableTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), buildLittleBobMinor());
        touch.addCharacters(0,0, "abc");
        touch.addCharacters(1,0, "abc");
        touch.addCharacters(0,1, "xyz");
        touch.addCharacters(1,1, "xyz");

        Parse parse = new AssignParseType().parse(touch.get());
        Parse result = new DefinitionInSplicedOrMain().parse(parse);

        assertParse(result.allCells().get(0,0), valid(3, DEFINITION));
        assertParse(result.allCells().get(0,1), valid(3, DEFINITION));
        assertParse(result.allCells().get(1,0), valid(3, DEFINITION));
        assertParse(result.allCells().get(1,1), valid(3, DEFINITION));
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

    private ObservableTouch buildAndParseSingleCellTouch(NotationBody... notations) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notations[0].getNumberOfWorkingBells());
        Arrays.stream(notations).forEach(touch::addNotation);
        touch.setTouchCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(true);
        touch.addDefinition("abc", "-1-");
        touch.addDefinition("xyz", "p");
        return touch;
    }

}