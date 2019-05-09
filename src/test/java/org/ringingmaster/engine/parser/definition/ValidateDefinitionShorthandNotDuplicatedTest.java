package org.ringingmaster.engine.parser.definition;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.assignparsetype.AssignParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.parse.Parse;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.LEAD_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;

public class ValidateDefinitionShorthandNotDuplicatedTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());

        Parse result = new AssignParseType()
                .andThen(new ValidateDefinitionShorthandNotDuplicated())
                .apply(composition.get());

        assertEquals(0, result.allCompositionCells().getRowSize());
        assertEquals(0, result.allCompositionCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor());
        composition.setCompositionType(COURSE_BASED);

        composition.addCharacters(MAIN_TABLE, 0,0, "CALL_POSITION");
        composition.addCharacters(MAIN_TABLE, 1,0, "MAIN_BODY");
        composition.addCharacters(MAIN_TABLE, 1,1, "SPLICE");
        composition.addCharacters(MAIN_TABLE, 2,0, "CALL");// To force the Parse to be replaced
        composition.addCharacters(MAIN_TABLE, 2,1, "CALL");// To force the Parse to be replaced

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
    public void duplicateDefinitionShorthandsMarkedInvalid() {
        MutableComposition composition = new MutableComposition();
        composition.addDefinition("3*", "-");
        composition.addCharacters(TableType.DEFINITION_TABLE, 1,SHORTHAND_COLUMN, "3*");

        Parse parse = new AssignParseType()
                .andThen(new ValidateDefinitionShorthandNotDuplicated())
                .apply(composition.get());


        Set<ImmutableArrayTable<ParsedCell>> definitionAsTables = parse.getDefinitionAsTables();
        assertEquals(2, definitionAsTables.size());
        for (ImmutableArrayTable<ParsedCell> definitionAsTable : definitionAsTables) {
            assertEquals("3*", definitionAsTable.get(0, SHORTHAND_COLUMN).getCharacters());
            assertParse(definitionAsTable.get(0, SHORTHAND_COLUMN), invalid(2, DEFINITION));
        }
    }

    @Test
    public void usagesOfDuplicateDefinitionsMarkedInvalid() {
        MutableComposition composition = new MutableComposition();
        composition.addNotation(buildPlainBobMinor());
        composition.setSpliced(true);
        composition.setCompositionType(LEAD_BASED);
        composition.addCharacters(MAIN_TABLE,0,0,"3*-");
        composition.addCharacters(MAIN_TABLE,0,1,"3*P");

        composition.addDefinition("3*", "-");
        composition.addCharacters(TableType.DEFINITION_TABLE, 1,SHORTHAND_COLUMN, "3*");
        composition.addDefinition("a", "3*");

        Parse parse = new AssignParseType()
                .andThen(new ValidateDefinitionShorthandNotDuplicated())
                .apply(composition.get());


        assertParse(parse.allCompositionCells().get(0,0),invalid(2, DEFINITION), valid(CALL));
        assertParse(parse.allCompositionCells().get(0,1),invalid(2, DEFINITION), valid(SPLICE));
        assertParse(parse.findDefinitionByShorthand("a").get().get(0,DEFINITION_COLUMN),invalid(2, DEFINITION));
    }

    @Test
    public void usagesOfDuplicateDefinitionsMarkedInvalid() {
        MutableComposition composition = new MutableComposition();
        composition.addNotation(buildPlainBobMinor());
        composition.setSpliced(true);
        composition.setCompositionType(LEAD_BASED);
        composition.addCharacters(MAIN_TABLE,0,0,"3*-");
        composition.addCharacters(MAIN_TABLE,0,1,"3*P");

        composition.addDefinition("3*", "-");
        composition.addCharacters(TableType.DEFINITION_TABLE, 1,SHORTHAND_COLUMN, "3*");
        composition.addDefinition("a", "3*");

        Parse parse = new AssignParseType()
                .andThen(new ValidateDefinitionShorthandNotDuplicated())
                .apply(composition.get());


        assertParse(parse.allCompositionCells().get(0,0),invalid(2, DEFINITION), valid(CALL));
        assertParse(parse.allCompositionCells().get(0,1),invalid(2, DEFINITION), valid(SPLICE));
        assertParse(parse.findDefinitionByShorthand("a").get().get(0,DEFINITION_COLUMN),invalid(2, DEFINITION));
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