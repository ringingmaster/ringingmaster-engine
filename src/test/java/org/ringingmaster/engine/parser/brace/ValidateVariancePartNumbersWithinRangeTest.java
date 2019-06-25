package org.ringingmaster.engine.parser.brace;

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
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class ValidateVariancePartNumbersWithinRangeTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
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
        composition.addCharacters(COMPOSITION_TABLE, 2,0, "abc");// To force the Parse to be replaced
        composition.addCharacters(COMPOSITION_TABLE, 2,1, "abc");// To force the Parse to be replaced

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertEquals(3, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }

    @Test
    public void parsesNoContentPairOfVarianceInSingleCell() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "[-o]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(section(1, VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid( VARIANCE_CLOSE));
    }

    @Test
    public void varianceInMainTableWithPartOf0Invalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0,"[-0s]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceInMainTableWithPartOf0WheneNotStStartOfCellInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0,"s[-0s]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(CALL), invalid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceInMainTableWithPartOf10001Invalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0,"[-10001s]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(section(VARIANCE_OPEN), section(6, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceInMainTableWithPartOf0OutOfOrderInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0,"[-1,0s]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(section(VARIANCE_OPEN), section(4, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceInMainTableWithPartOf1Valid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0,"[-1s]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceInMainTableWithPartOf10000Valid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0,"[-10000s]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(section(VARIANCE_OPEN), section(6, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceInDefinitionTableWithPartOf0Invalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addDefinition("DEF1", "[-0s]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVariancePartNumbersWithinRange())
                .apply(composition.get());

        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0, DEFINITION_COLUMN), invalid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE));
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
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        return composition;
    }
}
