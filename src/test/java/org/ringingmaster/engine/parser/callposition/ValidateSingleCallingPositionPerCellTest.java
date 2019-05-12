package org.ringingmaster.engine.parser.callingPosition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parser.callingposition.ValidateSingleCallingPositionPerCell;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;

public class ValidateSingleCallingPositionPerCellTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        Parse result = new AssignParseType()
                .andThen(new ValidateSingleCallingPositionPerCell())
                .apply(composition.get());

        assertEquals(0, result.allCompositionCells().getRowSize());
        assertEquals(0, result.allCompositionCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setSpliced(true);

        composition.addCharacters(COMPOSITION_TABLE, 0,0, "CALL_POSITION");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "MAIN_BODY");
        composition.addCharacters(COMPOSITION_TABLE, 1,1, "SPLICE");

        Parse result = new AssignParseType()
                .andThen(new ValidateSingleCallingPositionPerCell())
                .apply(composition.get());

        assertEquals(2, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }

    @Test
    public void parsingGoodCallingPositionTakesNoAction() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "W");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "H");

        Parse result = new AssignParseType()
                .andThen(new ValidateSingleCallingPositionPerCell())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(CALLING_POSITION));
        assertParse(result.allCompositionCells().get(0,0), valid(CALLING_POSITION));
    }

    @Test
    public void parsingDuplicateMarksSeconsAsInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "WH");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "-HW");

        Parse result = new AssignParseType()
                .andThen(new ValidateSingleCallingPositionPerCell())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(CALLING_POSITION), invalid(CALLING_POSITION));
        assertParse(result.allCompositionCells().get(0,1), unparsed() ,valid(CALLING_POSITION), invalid(CALLING_POSITION));
    }

    private Notation buildPlainBobMinor() {
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

    private MutableComposition buildSingleCellComposition(Notation notation) {
        MutableComposition composition = new MutableComposition();
        composition.setNumberOfBells(notation.getNumberOfWorkingBells());
        composition.addNotation(notation);
        composition.setCompositionType(CompositionType.COURSE_BASED);
        composition.setSpliced(false);
        return composition;
    }

}