package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.composition.TableType;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.SHORTHAND_COLUMN;

/**
 * @author stevelake
 */
public class AssignParseTypeDEFINITIONTest {

    @Test
    public void definitionUnparsedInCallingPoitionArea() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.setCheckingType(COURSE_BASED);
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), unparsed(4));
    }

    @Test
    public void definitionParsedInMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(4, DEFINITION));
    }

    @Test
    public void definitionParsedInSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1,"def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), valid(4, DEFINITION));
    }

    @Test
    public void correctlyParsesDefinitionTokenInMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-def1-");

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(CALL), valid(4, DEFINITION), valid(CALL));
    }

    @Test
    public void correctlyParsesDefinitionTokenInSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.setSpliced(true);
        composition.addCharacters(MAIN_TABLE, 0, 1, "Pdef1P");

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), valid(SPLICE), valid(4, DEFINITION), valid(SPLICE));
    }

    @Test
    public void definitionShorthandsParsedAsDefinitions() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), null);
        composition.addDefinition("def2","s");

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, SHORTHAND_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, SHORTHAND_COLUMN), valid(4, DEFINITION));
    }

    @Test
    public void unusedDefinitionParsedAsMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), null);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void definitionUsedInMainBodyAreaParsedAsMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1 def2");

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void definitionUsedInSpiceAreaParsedAsSpice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "s");
        composition.addCharacters(MAIN_TABLE, 0,1,"def1 def2");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed(), valid(SPLICE));
    }

    @Test
    public void definitionUsedInMainAndSpiceAreaParsedAsMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.addCharacters(MAIN_TABLE, 0,1,"def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void transitiveDefinitionInMainBodyParsedAsMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def3");
        composition.addDefinition("def3", "def2");
        composition.addDefinition("def2", "def1");

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.findDefinitionByShorthand("def3").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void transitiveDefinitionInSplicedParsedAsSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(TableType.MAIN_TABLE,0,1,"def3");
        composition.setSpliced(true);
        composition.addDefinition("def3", "def2");
        composition.addDefinition("def2", "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def3").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed(), valid(SPLICE));
    }

    @Test
    public void transitiveDefinitionInSplicedAndMainBodyParsedAsMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def33");
        composition.addCharacters(TableType.MAIN_TABLE,0,1,"def3");
        composition.setSpliced(true);
        composition.addDefinition("def3", "def2");
        composition.addDefinition("def2", "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def3").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, DEFINITION_COLUMN), valid(4, DEFINITION));
        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL), unparsed());
    }

    @Test
    public void multiplierDoesAddsDefaultCallWhenUsedInSpliceAndMainCells() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def2");
        composition.addCharacters(MAIN_TABLE, 0, 1, "def2");
        composition.setSpliced(true);
        composition.addDefinition("def2", "2");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, DEFINITION_COLUMN), valid(DEFAULT_CALL_MULTIPLIER));
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
        composition.setCheckingType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        composition.addDefinition("def1", "-P");
        return composition;
    }

}
