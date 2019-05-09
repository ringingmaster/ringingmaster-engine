package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALLING_POSITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class AssignParseTypeSPLICETest {

    @Test
    public void spliceIgnoredInCallingPoitionArea() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "WP");
        composition.setCompositionType(COURSE_BASED);
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(CALLING_POSITION), unparsed());
    }

    @Test
    public void spliceIgnoredInMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-P-");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(CALL), unparsed(), valid(CALL));
    }

    @Test
    public void spliceParsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1,"P");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), valid(SPLICE));
    }

    @Test
    public void spliceNameParsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1,"Plain Bob");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), valid(9, SPLICE));
    }

    @Test
    public void spliceNameFullyQualifiedParsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1,"Plain Bob Minor");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), valid(15, SPLICE));
    }

    @Test
    public void spliceIgnoredInUnusedDefinition() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void spliceUnparsedInDefinitionUsedInMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void spliceParsedInDefinitionUsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(SPLICE));
    }

    @Test
    public void spliceParsedInDefinitionUsedInSpliceAnMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void regexInSpliceMatchedLiteral() {
        Notation notation = NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain*Bob")
                .setFoldedPalindromeNotationShorthand("x16x16x16", "12")
                .addCall("Bob", "-", "14", true)
                .addCall("Single", "s", "1234", false)
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("*")
                .build();

        MutableComposition composition = buildSingleCellComposition(notation, "-");
        composition.addCharacters(MAIN_TABLE,0,1,"Plain*Bob");
        composition.addCharacters(MAIN_TABLE,1,1,"Plain*Bob Minor");
        composition.addCharacters(MAIN_TABLE,2,1,"*");

        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.allCompositionCells().get(0, 1), valid(9, SPLICE));
        assertParse(parse.allCompositionCells().get(1, 1), valid(15, SPLICE));
        assertParse(parse.allCompositionCells().get(2, 1), valid(1, SPLICE));
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
        composition.addDefinition("def1", "P");
        return composition;
    }

}
