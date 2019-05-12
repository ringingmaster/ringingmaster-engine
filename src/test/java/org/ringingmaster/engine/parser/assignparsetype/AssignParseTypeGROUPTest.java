package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;

import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN;
import static org.ringingmaster.engine.composition.TableType.COMPOSITION_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class AssignParseTypeGROUPTest {

    @Test
    public void groupIgnoredInCallingPoitionArea() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "()");
        composition.setCompositionType(COURSE_BASED);
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), unparsed(2));
    }

    @Test
    public void groupParsedInMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "()");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 0), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupUnparsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(COMPOSITION_TABLE,0,1,"()");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0, 1), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupParsedInUnusedDefinition() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupParsedInDefinitionUsedInMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupUnparsedInDefinitionUsedInSplice() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(COMPOSITION_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void groupParsedInDefinitionUsedInSpliceAnMainBody() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def1");
        composition.addCharacters(COMPOSITION_TABLE,0,1, "def1");
        composition.setSpliced(true);

        Parse parse = new AssignParseType().apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def1").get().get(0, DEFINITION_COLUMN), valid(MULTIPLIER_GROUP_OPEN), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void correctlyIdentifiesGroup() {
        MutableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "(-)s");
        Parse parse = new AssignParseType().apply(composition.get());
        assertParse(parse.allCompositionCells().get(0,0), valid(MULTIPLIER_GROUP_OPEN), valid(CALL), valid(MULTIPLIER_GROUP_CLOSE), valid(CALL));
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
            composition.addCharacters(COMPOSITION_TABLE, 0, 0, characters);
        }
        composition.addNotation(notation);
        composition.setCompositionType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        composition.addDefinition("def1", "()");
        return composition;
    }

}
