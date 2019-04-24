package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;

import static org.ringingmaster.engine.composition.TableType.DEFINITION_TABLE;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeWHITESPACETest {

    @Test
    public void correctlyParsesWhitespaceInCallingArea() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "W H");
        composition.setCompositionType(CompositionType.COURSE_BASED);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 0), valid(CALLING_POSITION), unparsed(1), valid(CALLING_POSITION));
    }

    @Test
    public void correctlyParsesWhitespaceInMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "- Bob");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 0), valid(CALL), unparsed(1), valid(3, CALL));
    }

    @Test
    public void correctlyParsesWhitespaceInSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(TableType.MAIN_TABLE,0,1,"P P ");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 1), valid(SPLICE), unparsed(1), valid(SPLICE), unparsed(1));
    }

    @Test
    public void correctlyParsesWhitespaceInUnusedDefinition() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed(1), valid(CALL));
    }

    @Test
    public void correctlyParsesWhitespaceInDefinitionUsedInMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed(1), valid(CALL));
    }

    @Test
    public void correctlyParsesWhitespaceInDefinitionUsedInSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed(), unparsed(1), unparsed());
    }

    @Test
    public void correctlyParsesWhitespaceInDefinitionUsedInMainBodySplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed(1), valid(CALL));
    }

    @Test
    public void correctlyParsesWhitespaceInDefinitionShorthand() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(DEFINITION_TABLE,1,0, " de f2 ");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("de f2").get().get(0, SHORTHAND_COLUMN), unparsed(1), valid(5, DEFINITION), unparsed(1));
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

    private ObservableComposition buildSingleCellComposition(Notation notation, String characters) {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(notation.getNumberOfWorkingBells());
        if (characters != null) {
            composition.addCharacters(MAIN_TABLE, 0, 0, characters);
        }
        composition.addNotation(notation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        composition.addDefinition("def1", "- -");
        return composition;
    }

}
