package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
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

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class AssignParseTypeCALLTest {

    @Test
    public void callIgnoredInCallingPoitionArea() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.setCompositionType(COURSE_BASED);
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), unparsed());
    }

    @Test
    public void callParsedInMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-P-");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(CALL), unparsed(), valid(CALL));
    }

    @Test
    public void callUnparsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1,"-");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), unparsed());
    }

    @Test
    public void callParsedInUnusedDefinition() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL));
    }

    @Test
    public void callUnparsedInDefinitionUsedInMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL));
    }

    @Test
    public void callUnparsedInDefinitionUsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void callParsedInDefinitionUsedInSpliceAnMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(CALL));
    }

    @Test
    public void regexInCallMatchedLiteral() {

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
        MutableComposition composition = buildSingleCellComposition(notation, "3*?");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 0), valid(2, CALL), valid(CALL));
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

    private MutableComposition buildSingleCellComposition(Notation notation, String characters) {
        MutableComposition composition = new MutableComposition();
        composition.setNumberOfBells(notation.getNumberOfWorkingBells());
        if (characters != null) {
            composition.addCharacters(MAIN_TABLE, 0, 0, characters);
        }
        composition.addNotation(notation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        composition.addDefinition("def1", "-");
        return composition;
    }

}
