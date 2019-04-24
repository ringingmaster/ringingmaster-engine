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
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

public class ValidateMultiplierGroupMatchingBraceTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
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
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertEquals(3, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }

    @Test
    public void parsesNoContentPairOfGroupInSingleCell() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "()");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupInSingleCellInWrongOrderInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, ")(");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid( MULTIPLIER_GROUP_CLOSE), invalid( MULTIPLIER_GROUP_OPEN));
    }

    @Test
    public void groupOnMultiLineCellInWrongOrderInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, ")");
        composition.addCharacters(MAIN_TABLE, 1,0, "(");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid( MULTIPLIER_GROUP_CLOSE));
        assertParse(result.allCompositionCells().get(1,0), invalid( MULTIPLIER_GROUP_OPEN));
    }

    @Test
    public void nestedGroupInSingleCellIsValid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "(())");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_CLOSE), valid( MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void nestedGroupOnMultiLineIsValid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "(");
        composition.addCharacters(MAIN_TABLE, 0,1, "(");
        composition.addCharacters(MAIN_TABLE, 1,0, "))");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid( MULTIPLIER_GROUP_OPEN));
        assertParse(result.allCompositionCells().get(0,1), valid( MULTIPLIER_GROUP_OPEN));
        assertParse(result.allCompositionCells().get(1,0), valid( MULTIPLIER_GROUP_CLOSE), valid( MULTIPLIER_GROUP_CLOSE));
    }


    @Test
    public void additionalOpeningGroupInSingleCellIsInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "(()");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void additionalOpeningGroupInMultiCellIsInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "(");
        composition.addCharacters(MAIN_TABLE, 0,1, "(");
        composition.addCharacters(MAIN_TABLE, 1,0, ")");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid( MULTIPLIER_GROUP_OPEN));
        assertParse(result.allCompositionCells().get(0,1), valid( MULTIPLIER_GROUP_OPEN));
        assertParse(result.allCompositionCells().get(1,0), valid( MULTIPLIER_GROUP_CLOSE));
    }


    @Test
    public void nestedGroupWithSplicedAssignsInvalidityToCorrect() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setSpliced(true);
        composition.addCharacters(MAIN_TABLE, 0,0, "(");
        composition.addCharacters(MAIN_TABLE, 0,1, "(");//spliced
        composition.addCharacters(MAIN_TABLE, 1,0, "-");
        composition.addCharacters(MAIN_TABLE, 1,1, ")");//spliced
        composition.addCharacters(MAIN_TABLE, 2,0, ")");
        composition.addCharacters(MAIN_TABLE, 2,1, ")");//spliced

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid( MULTIPLIER_GROUP_OPEN));
        assertParse(result.allCompositionCells().get(0,1), valid( MULTIPLIER_GROUP_OPEN));
        assertParse(result.allCompositionCells().get(1,0), valid( CALL));
        assertParse(result.allCompositionCells().get(1,1), valid( MULTIPLIER_GROUP_CLOSE));
        assertParse(result.allCompositionCells().get(2,0), valid( MULTIPLIER_GROUP_CLOSE));
        assertParse(result.allCompositionCells().get(2,1), invalid( MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupsWithinCourseBasedInvalid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setCompositionType(CompositionType.COURSE_BASED);
        composition.addCharacters(MAIN_TABLE, 0,0, "(");
        composition.addCharacters(MAIN_TABLE, 0,1, ")");
        composition.addCharacters(MAIN_TABLE, 1,0, "-");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), unparsed());
        assertParse(result.allCompositionCells().get(0,1), unparsed());
    }

    @Test
    public void groupsWithinDefinitionValid() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addDefinition("DEF1", "(-)");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.findDefinitionByShorthand("DEF1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(CALL ), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void nestingDepthOkAt4() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "((((-))))");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_OPEN),
                valid( CALL),
                valid( MULTIPLIER_GROUP_CLOSE), valid( MULTIPLIER_GROUP_CLOSE), valid( MULTIPLIER_GROUP_CLOSE), valid( MULTIPLIER_GROUP_CLOSE)
        );
    }

    @Test
    public void nestingDepthInvalidAt5() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.addCharacters(MAIN_TABLE, 0,0, "(((((-)))))");

        Parse result = new AssignParseType()
                .andThen(new ValidateMultiplierGroupMatchingBrace())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0),
                valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_OPEN), valid( MULTIPLIER_GROUP_OPEN), invalid( MULTIPLIER_GROUP_OPEN),
                valid( CALL),
                valid( MULTIPLIER_GROUP_CLOSE), valid( MULTIPLIER_GROUP_CLOSE), valid( MULTIPLIER_GROUP_CLOSE), valid( MULTIPLIER_GROUP_CLOSE), invalid( MULTIPLIER_GROUP_CLOSE)
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

    private ObservableComposition buildSingleCellComposition(Notation... notations) {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(notations[0].getNumberOfWorkingBells());
        Arrays.stream(notations).forEach(composition::addNotation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        return composition;
    }
}