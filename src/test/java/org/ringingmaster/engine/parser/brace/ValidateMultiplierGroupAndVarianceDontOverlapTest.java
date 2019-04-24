package org.ringingmaster.engine.parser.brace;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.ObservableComposition;
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
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;

public class ValidateMultiplierGroupAndVarianceDontOverlapTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
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
        composition.addCharacters(MAIN_TABLE, 2,0, "abc");// To force the Parse to be replaced
        composition.addCharacters(MAIN_TABLE, 2,1, "abc");// To force the Parse to be replaced

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(composition.get());

        assertEquals(3, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }



    @Test
    public void VarianceEnclosedByGroupValid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "([-o])");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                valid( MULTIPLIER_GROUP_OPEN), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid( VARIANCE_CLOSE), valid( MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void GroupEnclosedByVarianceValid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "[-o()]");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_CLOSE), valid( VARIANCE_CLOSE));
    }

    @Test
    public void GroupOverlappingVarianceInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "([-e)]");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                invalid( MULTIPLIER_GROUP_OPEN), invalid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), invalid( MULTIPLIER_GROUP_CLOSE), invalid( VARIANCE_CLOSE));
    }

    @Test
    public void VarianceOverlappingGroupInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "[-e(])");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                invalid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), invalid( MULTIPLIER_GROUP_OPEN), invalid( VARIANCE_CLOSE), invalid( MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void MultiVarianceOverlappingGroupInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "[-o(][-e)]");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), invalid( MULTIPLIER_GROUP_OPEN), invalid( VARIANCE_CLOSE), invalid("Variances and Groups can't overlap", section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), invalid( MULTIPLIER_GROUP_CLOSE), valid( VARIANCE_CLOSE));
    }

    @Test
    public void ignoresAlreadyInvalidBrace() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "(])"); //this sequence caused crash

        Parse result = new AssignParseType()
//                this line will pre-invalidate the closing variance brace.
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                valid( MULTIPLIER_GROUP_OPEN), invalid( VARIANCE_CLOSE), valid(MULTIPLIER_GROUP_CLOSE));
    }


    @Test
    public void closingBraceBeforeOpeningBraceDoesNotThrow() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "(])]"); //this sequence caused crash

        Parse result = new AssignParseType()
                .andThen(new ValidateVarianceMatchingBraceLogic())
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .andThen(new ValidateMultiplierGroupAndVarianceDontOverlap())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                valid(MULTIPLIER_GROUP_OPEN), invalid(VARIANCE_CLOSE), valid(MULTIPLIER_GROUP_CLOSE), invalid(VARIANCE_CLOSE));
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

    private ObservableComposition buildSingleCellComposition(Notation... notations) {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(notations[0].getNumberOfWorkingBells());
        Arrays.stream(notations).forEach(composition::addNotation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        return composition;
    }
}