package org.ringingmaster.engine.parser.callposition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.composition.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;

public class ValidateSingleCallPositionPerCellTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        Parse result = new AssignParseType()
                .andThen(new ValidateSingleCallPositionPerCell())
                .apply(composition.get());

        assertEquals(0, result.allCompositionCells().getRowSize());
        assertEquals(0, result.allCompositionCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setSpliced(true);

        composition.addCharacters(MAIN_TABLE, 0,0, "CALL_POSITION");
        composition.addCharacters(MAIN_TABLE, 1,0, "MAIN_BODY");
        composition.addCharacters(MAIN_TABLE, 1,1, "SPLICE");

        Parse result = new AssignParseType()
                .andThen(new ValidateSingleCallPositionPerCell())
                .apply(composition.get());

        assertEquals(2, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }

    @Test
    public void parsingGoodCallPositionTakesNoAction() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "W");
        composition.addCharacters(MAIN_TABLE, 0,1, "H");

        Parse result = new AssignParseType()
                .andThen(new ValidateSingleCallPositionPerCell())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(CALLING_POSITION));
        assertParse(result.allCompositionCells().get(0,0), valid(CALLING_POSITION));
    }

    @Test
    public void parsingDuplicateMarksSeconsAsInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "WH");
        composition.addCharacters(MAIN_TABLE, 0,1, "-HW");

        Parse result = new AssignParseType()
                .andThen(new ValidateSingleCallPositionPerCell())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(CALLING_POSITION), invalid(CALLING_POSITION));
        assertParse(result.allCompositionCells().get(0,1), unparsed() ,valid(CALLING_POSITION), invalid(CALLING_POSITION));
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

    private ObservableComposition buildSingleCellComposition(NotationBody notationBody) {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        composition.addNotation(notationBody);
        composition.setCheckingType(CheckingType.COURSE_BASED);
        composition.setSpliced(false);
        return composition;
    }

}