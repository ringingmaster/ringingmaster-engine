package org.ringingmaster.engine.parser.callingposition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ValidateCellInColumnWithValidCallPositionTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateCellInColumnWithValidCallPosition())
                .apply(composition.get());

        assertEquals(0, result.allCompositionCells().getRowSize());
        assertEquals(0, result.allCompositionCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setSpliced(true);

        composition.addCharacters(COMPOSITION_TABLE, 0 ,0, "CALL_POSITION");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "MAIN_BODY");
        composition.addCharacters(COMPOSITION_TABLE, 1,1, "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 2,0, "abc");// To force the Parse to be replaced
        composition.addCharacters(COMPOSITION_TABLE, 2,1, "abc");// To force the Parse to be replaced

        Parse result = new AssignParseType()
                .andThen(new ValidateCellInColumnWithValidCallPosition())
                .apply(composition.get());

        assertEquals(3, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }

    @Test
    public void columnWithCallingPositionIsValid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0 ,0, "W");
        composition.addCharacters(COMPOSITION_TABLE, 1 ,0, "s");

        Parse result = new AssignParseType()
                .andThen(new ValidateCellInColumnWithValidCallPosition())
                .apply(composition.get());


        assertParse(result.allCompositionCells().get(0,0), valid(CALLING_POSITION));
        assertParse(result.allCompositionCells().get(1,0), valid(CALL));
    }

    @Test
    public void columnWithoutCallingPositionIsInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0 ,0, "m");
        composition.addCharacters(COMPOSITION_TABLE, 1 ,0, "s");

        Parse result = new AssignParseType()
                .andThen(new ValidateCellInColumnWithValidCallPosition())
                .apply(composition.get());


        assertParse(result.allCompositionCells().get(0,0), unparsed());
        assertParse(result.allCompositionCells().get(1,0), invalid(CALL));
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

    private MutableComposition buildSingleCellComposition(Notation... notations) {
        MutableComposition composition = new MutableComposition();
        composition.setNumberOfBells(notations[0].getNumberOfWorkingBells());
        Arrays.stream(notations).forEach(composition::addNotation);
        composition.setCompositionType(CompositionType.COURSE_BASED);
        composition.setSpliced(false);
        return composition;
    }

}