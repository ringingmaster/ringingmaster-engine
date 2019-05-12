package org.ringingmaster.engine.parser.definition;

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
import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

public class ValidateDefinitionIsUsedSplicedOrMainTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        Parse parse = new AssignParseType().apply(composition.get());
        Parse result = new ValidateDefinitionIsUsedSplicedOrMain().apply(parse);

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
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
                .apply(composition.get());

        assertEquals(3, result.allCompositionCells().getRowSize());
        assertEquals(2, result.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allCompositionCells().get(1,1).getCharacters());
    }

    @Test
    public void differentDefinitionsValidInMainAndSpliced() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "SPLICE");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(0,1), valid(6, DEFINITION));
    }

    @Test
    public void usingSameDefinitionInMainAndSplicedSetsBothInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "CALL");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(1,0), valid(6, DEFINITION));
        assertParse(result.allCompositionCells().get(0,1), invalid(4, DEFINITION));
    }

    @Test
    public void usingSameDefinitionInEitherMainOrSplicedIsValid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 1,1, "SPLICE");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(1,0), valid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(0,1), valid(6, DEFINITION));
        assertParse(result.allCompositionCells().get(1,1), valid(6, DEFINITION));
    }

    @Test
    public void embeddedDefinitionInMainUsedInSplicedInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addDefinition("IN_MAIN", "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "IN_MAIN");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(0,1), invalid(6, DEFINITION));
        assertParse(result.findDefinitionByShorthand("IN_MAIN").get().get(0, DEFINITION_COLUMN), invalid(6, DEFINITION));
    }

    @Test
    public void embeddedDefinitionInMainTransitivelyUsedInSplicedInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addDefinition("IN_MAIN_1", "IN_MAIN_2");
        composition.addDefinition("IN_MAIN_2", "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 1,0, "IN_MAIN_1");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), valid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(0,1), invalid(6, DEFINITION));
        assertParse(result.findDefinitionByShorthand("IN_MAIN_2").get().get(0, DEFINITION_COLUMN), invalid(6, DEFINITION));
    }

    @Test
    public void embeddedDefinitionInSplicedUsedInMainInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addDefinition("IN_SPICE", "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 0,1, "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 1,1, "IN_SPICE");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0,0), invalid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(0,1), valid(6, DEFINITION));
        assertParse(result.findDefinitionByShorthand("IN_SPICE").get().get(0, DEFINITION_COLUMN), invalid(4, DEFINITION));
    }

    @Test
    public void embeddedDefinitionInSplicedTransitivelyUsedInMainInvalid() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), buildLittleBobMinor());
        composition.addDefinition("IN_SPICE_1", "IN_SPICE_2");
        composition.addDefinition("IN_SPICE_2", "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 0, 0, "CALL");
        composition.addCharacters(COMPOSITION_TABLE, 0, 1, "SPLICE");
        composition.addCharacters(COMPOSITION_TABLE, 1, 1, "IN_SPICE_1");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
                .apply(composition.get());

        assertParse(result.allCompositionCells().get(0, 0), invalid(4, DEFINITION));
        assertParse(result.allCompositionCells().get(0, 1), valid(6, DEFINITION));
        assertParse(result.findDefinitionByShorthand("IN_SPICE_2").get().get(0, DEFINITION_COLUMN), invalid(4, DEFINITION));
    }

    @Test
    public void canOperateWithDefinitionWithOnlyShorthandDoesNotTHrow() {
        MutableComposition composition = new MutableComposition();
        composition.setCompositionType(LEAD_BASED);
        composition.addCharacters(COMPOSITION_TABLE, 0,0, "DEF_1");
        composition.addCharacters(DEFINITION_TABLE, 0,0, "DEF_1");

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionIsUsedSplicedOrMain())
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