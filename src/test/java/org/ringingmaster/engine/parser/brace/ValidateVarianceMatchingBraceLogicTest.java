package org.ringingmaster.engine.parser.brace;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

public class ValidateVarianceMatchingBraceLogicTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .apply(composition.get());

        assertEquals(0, result.allCompositionCells().getRowSize());
        assertEquals(0, result.allCompositionCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setSpliced(true);

        composition.addCharacters(MAIN_TABLE, 0,0, "CALL_POSITION");
        composition.addCharacters(MAIN_TABLE, 1,0, "MAIN_BODY");
        composition.addCharacters(MAIN_TABLE, 1,1, "SPLICE");
        composition.addCharacters(MAIN_TABLE, 2,0, "abc");// To force the Parse to be replaced
        composition.addCharacters(MAIN_TABLE, 2,1, "abc");// To force the Parse to be replaced

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
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
        composition.addCharacters(MAIN_TABLE, 0,0, "[-o]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(section(1, VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid( VARIANCE_CLOSE));
    }

    @Test
    public void varianceInSingleCellInWrongOrderInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "][-o");

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid( VARIANCE_CLOSE, "No matching opening variance brace"), invalid("No matching closing variance brace", section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)));
    }

    @Test
    public void varianceOnMultiLineCellInWrongOrderInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "]");
        composition.addCharacters(MAIN_TABLE, 1,0, "[-0");

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid( VARIANCE_CLOSE));
        assertParse(result.allCompositionCells().get(1,0), invalid("No matching closing variance brace", section(1, VARIANCE_OPEN), section(2, VARIANCE_DETAIL)));
    }

    @Test
    public void variancesWithinCourseBasedInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setCompositionType(CompositionType.COURSE_BASED);
        composition.addCharacters(MAIN_TABLE, 0,0, "[");
        composition.addCharacters(MAIN_TABLE, 0,1, "]");
        composition.addCharacters(MAIN_TABLE, 1,0, "-");

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), unparsed());
        assertParse(result.allCompositionCells().get(0,1), unparsed());
    }

    @Test
    public void varianceWithinDefinitionValid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addDefinition("DEF1", "[-e-]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .apply(composition.get());

        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0, DEFINITION_COLUMN), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE));
    }

    @Test
    public void nestingVarianceInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "[-o[-e-]]");

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                valid(section(1, VARIANCE_OPEN), section(2, VARIANCE_DETAIL)),
                    invalid("Nesting depth greater than the 1 allowed for variance", section(1, VARIANCE_OPEN), section(2, VARIANCE_DETAIL)),
                        valid( CALL),
                    valid( VARIANCE_CLOSE),
                invalid( VARIANCE_CLOSE)
        );
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