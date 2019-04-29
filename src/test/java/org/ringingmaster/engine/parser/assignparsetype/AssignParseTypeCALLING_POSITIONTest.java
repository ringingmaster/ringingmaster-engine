package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;

import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeCALLING_POSITIONTest {

    @Test
    public void callingPositionParsedInCallingPoitionArea() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "WH");
        composition.setCompositionType(COURSE_BASED);
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(CALLING_POSITION), valid(CALLING_POSITION));
    }

    @Test
    public void callingPositionIgnoredInMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "W");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), unparsed());
    }

    @Test
    public void callingPositionUnparsedInSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1,"W");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), unparsed());
    }

    @Test
    public void callingPositionUnparsedInUnusedDefinition() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "W");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void callingPositionUnparsedInDefinitionUsedInMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void callingPositionParsedInDefinitionUsedInSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void callingPositionParsedInDefinitionUsedInSpliceAnMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void ignoreOtherCharactersInCallingPositionCell() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "bHd");
        composition.setCompositionType(COURSE_BASED);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), unparsed(), valid(CALLING_POSITION), unparsed());
    }

    @Test
    public void regexInCallingPositionMatchedLiteral() {
        Notation notation = NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain*Bob")
                .setFoldedPalindromeNotationShorthand("x16x16x16", "12")
                .addCall("Bob", "-", "14", true)
                .addCall("Single", "s", "1234", false)
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W*", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("*")
                .build();

        ObservableComposition composition = buildSingleCellComposition(notation, "W*");

        composition.setSpliced(false);
        composition.setCompositionType(COURSE_BASED);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 0), valid(2, CALLING_POSITION));
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
        composition.addDefinition("def1", "W");
        return composition;
    }

}
