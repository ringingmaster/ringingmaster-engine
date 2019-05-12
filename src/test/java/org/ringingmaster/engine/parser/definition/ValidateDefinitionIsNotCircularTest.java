package org.ringingmaster.engine.parser.definition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

public class ValidateDefinitionIsNotCircularTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsNotCircular())
                .apply(composition.get());

        assertEquals(0, result.allCompositionCells().getRowSize());
        assertEquals(0, result.allCompositionCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setCompositionType(COURSE_BASED);

        composition.addCharacters(COMPOSITION_TABLE, 0,0, "CALL_POSITION");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "MAIN_BODY");
        composition.addCharacters(COMPOSITION_TABLE, 1,1, "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 2,0, "CALL");// To force the Parse to be replaced
        composition.addCharacters(COMPOSITION_TABLE, 2,1, "CALL");// To force the Parse to be replaced

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsNotCircular())
                .apply(composition.get());

        assertEquals(3, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }

    @Test
    public void circularDependencyInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addDefinition("DEF_1", "DEF_2");
        composition.addDefinition("DEF_2", "DEF_3");
        composition.addDefinition("DEF_3", "DEF_1");
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "DEF_1");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsNotCircular())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_1").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_2").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_3").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
    }

    @Test
    public void circularDependencyLeavesAdditionalPathValid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addDefinition("DEF_1", "DEF_2DEF_3");
        composition.addDefinition("DEF_2", "DEF_1");
        composition.addDefinition("DEF_3", "-");
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "DEF_1");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsNotCircular())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_1").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION), valid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_2").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
        assertParse(result.findDefinitionByShorthand("DEF_3").get().get(0, DEFINITION_COLUMN), valid(CALL));
    }

    @Test
    public void circularDependencyInsideSingleDefInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addDefinition("DEF_1", "DEF_1");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsNotCircular())
                .apply(composition.get());

        assertParse(result.findDefinitionByShorthand("DEF_1").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
    }

    @Test
    public void circularDependencyInsideSingleDefWhenUsedInMainInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "-");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "DEF_1");
        composition.addDefinition("DEF_1", "DEF_1");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsNotCircular())
                .apply(composition.get());

        assertParse(result.findDefinitionByShorthand("DEF_1").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
    }

    @Test
    public void circularDependencyInsideSingleDefWhenUsedInSpliceInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "DEF_1");
        composition.addDefinition("DEF_1", "DEF_1");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsNotCircular())
                .apply(composition.get());

        assertParse(result.findDefinitionByShorthand("DEF_1").get().get(0, DEFINITION_COLUMN), invalid(5, DEFINITION));
    }

    @Test
    public void canOperateWithDefinitionWithOnlyShorthandDoesNotTHrow() {
        MutableComposition composition = new MutableComposition();
        composition.setCompositionType(LEAD_BASED);
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "DEF_1");
        composition.addCharacters(DEFINITION_TABLE, 0,0, "DEF_1");

        new AssignParseType()
                .andThen(new ValidateDefinitionIsNotCircular())
                .apply(composition.get());
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
                .setSpliceIdentifier("P")
                .build();
    }

    private Notation buildLittleBobMinor() {
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

    private MutableComposition buildSingleCellComposition(Notation... notations) {
        MutableComposition composition = new MutableComposition();
        composition.setNumberOfBells(notations[0].getNumberOfWorkingBells());
        Arrays.stream(notations).forEach(composition::addNotation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(true);
        composition.addDefinition("CALL", "-1-");
        composition.addDefinition("SPLICE", "P");
        return composition;
    }

}