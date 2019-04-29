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
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypePLAIN_LEADTest {

    @Test
    public void plainLeadInCallingAreaNotParsed() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "p");
        composition.setCompositionType(CompositionType.COURSE_BASED);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 0), unparsed());
    }

    @Test
    public void correctlyParsesPlainLeadTokenInMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-p-");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 0), valid(CALL), valid(PLAIN_LEAD), valid(CALL));
    }

    @Test
    public void doesNotParsePlainLeadInSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1,"p");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 1), unparsed());
    }

    @Test
    public void correctlyParsesPlainLeadInUnusedDefinition() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(PLAIN_LEAD));
    }

    @Test
    public void correctlyParsesPlainLeadInDefinitionUsedInMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(PLAIN_LEAD));
    }

    @Test
    public void correctlyParsesPlainLeadInDefinitionUsedInSplice() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void correctlyParsesPlainLeadInDefinitionUsedInSpliceAndMainBody() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(PLAIN_LEAD));
    }

    @Test
    public void plainLeadUnparsedInMainBodyWhenCourseBased() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "W");
        composition.addCharacters(MAIN_TABLE, 1,0,"p");
        composition.setCompositionType(COURSE_BASED);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.mainBodyCells().get(0,0), unparsed());
    }

    @Test
    public void regexInPlainLeadMatchedLiteral() {

        Notation notation = NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("x16x16x16", "12")
                .addCall("3*", "?", "14", true)
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("P")
                .build();
        ObservableComposition composition = buildSingleCellComposition(notation, "*P*");
        composition.setPlainLeadToken("P*");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 0), unparsed(), valid(2,PLAIN_LEAD));
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
        composition.addDefinition("def1", "p");
        return composition;
    }

}
