package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class AssignParseTypeVARIANCETest {

    @Test
    public void varianceIgnoredInCallingPoitionArea() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "[-o]");
        composition.setCompositionType(COURSE_BASED);
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), unparsed(4));
    }

    @Test
    public void varianceParsedInMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "[-o]");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceUnparsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1,"[]");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), unparsed(2));
    }

    @Test
    public void varianceParsedInUnusedDefinition() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceParsedInDefinitionUsedInMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(VARIANCE_CLOSE));
    }

    @Test
    public void varianceUnparsedInDefinitionUsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), unparsed(4));
    }

    @Test
    public void varianceParsedInDefinitionUsedInSpliceAnMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.addCharacters(MAIN_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(VARIANCE_CLOSE));
    }

    @Test
    public void correctlyIdentifiesVariance() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "[-o-]s");
        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0,0), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(CALL), valid(VARIANCE_CLOSE), valid(CALL));
    }

    @Test
    public void identifiesVarianceTypeWhenNoContent() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "[-2]");
        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0,0), valid(section(VARIANCE_OPEN), section(2, VARIANCE_DETAIL)), valid(VARIANCE_CLOSE));
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
        composition.addDefinition("def1", "[-o]");
        return composition;
    }

}
