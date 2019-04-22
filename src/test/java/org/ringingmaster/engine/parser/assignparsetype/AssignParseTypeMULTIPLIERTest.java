package org.ringingmaster.engine.parser.assignparsetype;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.section;
import static org.ringingmaster.engine.parser.AssertParse.unparsed;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_CLOSE;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_DETAIL;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.WHITESPACE;
import static org.ringingmaster.engine.composition.TableType.MAIN_TABLE;
import static org.ringingmaster.engine.composition.compositiontype.CompositionType.COURSE_BASED;
import static org.ringingmaster.engine.composition.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignParseTypeMULTIPLIERTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), null);
        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertEquals(0, parse.allCompositionCells().getRowSize());
        assertEquals(0, parse.allCompositionCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), null);
        composition.setCheckingType(COURSE_BASED);

        composition.addCharacters(MAIN_TABLE, 0,0, "CALL_POSITION");
        composition.addCharacters(MAIN_TABLE, 1,0, "MAIN_BODY");
        composition.addCharacters(MAIN_TABLE, 1,1, "SPLICE");
        composition.addCharacters(MAIN_TABLE, 2,0, "CALL");// To force the Parse to be replaced
        composition.addCharacters(MAIN_TABLE, 2,1, "CALL");// To force the Parse to be replaced

        Parse parse = new AssignParseType()
 
                .apply(composition.get());

        assertEquals(3, parse.allCompositionCells().getRowSize());
        assertEquals(2, parse.allCompositionCells().getColumnSize());
        assertEquals("CALL_POSITION", parse.allCompositionCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", parse.allCompositionCells().get(1,0).getCharacters());
        assertEquals("SPLICE", parse.allCompositionCells().get(1,1).getCharacters());
    }


    @Test
	public void correctlyParseSingleDefaultCallMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-2");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(CALL), valid(DEFAULT_CALL_MULTIPLIER));
	}

	@Test
	public void correctlyParseMultiDefaultCallMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-22");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(CALL), valid(2,DEFAULT_CALL_MULTIPLIER));
	}

    @Test
    public void correctlyParseDefaultCallMultiplierBeforeWhitespace() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "2 ");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(DEFAULT_CALL_MULTIPLIER), valid(WHITESPACE));
    }

    @Test
    public void correctlyParseDefaultCallMultiplierBeforeVariance() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "6[-o7]");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(DEFAULT_CALL_MULTIPLIER), valid(section(VARIANCE_OPEN), section(2,VARIANCE_DETAIL)), valid(DEFAULT_CALL_MULTIPLIER), valid(VARIANCE_CLOSE));
    }

    @Test
    public void correctlyParseCallMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "2-");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(section(CALL_MULTIPLIER), section(CALL)));
    }

    @Test
    public void correctlyParseMultiCallMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "28-");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(section(2,CALL_MULTIPLIER), section(CALL)));
    }

    @Test
    public void correctlyParseGroupMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "6(7)");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(section(MULTIPLIER_GROUP_OPEN_MULTIPLIER), section(MULTIPLIER_GROUP_OPEN)), valid(DEFAULT_CALL_MULTIPLIER), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void correctlyParseMultiGroupMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "624(");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(section(3, MULTIPLIER_GROUP_OPEN_MULTIPLIER), section(MULTIPLIER_GROUP_OPEN)));
    }

    @Test
    public void correctlyParsePlainLeadMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "3p");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(section(PLAIN_LEAD_MULTIPLIER), section(PLAIN_LEAD)));
    }

    @Test
    public void correctlyParseMultiPlainLeadMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "434p");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(section(3, PLAIN_LEAD_MULTIPLIER), section(PLAIN_LEAD)));
    }

    @Test
    public void correctlyParseDefinitionMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "2def1");

        Parse parse = new AssignParseType()
 
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(section(1, DEFINITION_MULTIPLIER), section(4, DEFINITION)));
    }

    @Test
    public void correctlyParseMultiDefinitionMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "243def1");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,0), valid(section(3, DEFINITION_MULTIPLIER), section(4, DEFINITION)));
    }

    @Test
    public void correctlyParseSpliceMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "DUMMY");
        composition.addCharacters(MAIN_TABLE, 0, 1, "2p");
        composition.setSpliced(true);

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,1), valid(section(1, SPLICE_MULTIPLIER), section(SPLICE)));
    }

    @Test
    public void correctlyParseMultiSpliceMultiplier() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "DUMMY");
        composition.addCharacters(MAIN_TABLE, 0, 1, "392p");
        composition.setSpliced(true);

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,1), valid(section(3, SPLICE_MULTIPLIER), section(SPLICE)));
    }

    @Test
    public void standAloneNumbersInSplicedUnparsed() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "DUMMY");
        composition.addCharacters(MAIN_TABLE, 0, 1, "392-");
        composition.setSpliced(true);

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.allCompositionCells().get(0,1), unparsed(4));
    }

    @Test
    public void multiplierWorksInDefinition() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def2");

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def2").get().get(0,DEFINITION_COLUMN), valid(DEFAULT_CALL_MULTIPLIER));
    }

    @Test
    public void multiplierDoesNotAddDefaultCallWhenUsedOnlyInSpliceInDefinition() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "-");
        composition.addCharacters(MAIN_TABLE, 0, 1, "def2");
        composition.setSpliced(true);

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def2").get().get(0,DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void multiplierDoesAddDefaultCallWhenUsedInMainCellsInDefinition() {
        ObservableComposition composition = buildSingleCellComposition(buildPlainBobMinor(), "def2");
        composition.addCharacters(MAIN_TABLE, 0, 1, "-");
        composition.setSpliced(true);

        Parse parse = new AssignParseType()
                .apply(composition.get());

        assertParse(parse.findDefinitionByShorthand("def2").get().get(0,DEFINITION_COLUMN), valid(DEFAULT_CALL_MULTIPLIER));
    }

    //TODO need tests of definition area

    private NotationBody buildPlainBobMinor() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Plain Bob")
                .setFoldedPalindromeNotationShorthand("x16x16x16", "12")
                .addCall("Bob", "-", "14", true)
                .addCall("Single", "s", "1234", false)
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("p")
                .build();
    }

    private NotationBody buildLittleBobMinorWithNoDefaultCall() {
        return NotationBuilder.getInstance()
                .setNumberOfWorkingBells(NumberOfBells.BELLS_6)
                .setName("Little Bob")
                .setFoldedPalindromeNotationShorthand("x16x14", "12")
                .addCallInitiationRow(7)
                .addMethodCallingPosition("W", 7, 1)
                .addMethodCallingPosition("H", 7, 2)
                .setSpliceIdentifier("l")
                .build();
    }

    private ObservableComposition buildSingleCellComposition(NotationBody notationBody, String characters) {
        ObservableComposition composition = new ObservableComposition();
        composition.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        if (characters != null) {
            composition.addCharacters(MAIN_TABLE, 0, 0, characters);
        }
        composition.addNotation(notationBody);
        composition.setCheckingType(CompositionType.LEAD_BASED);
        composition.setSpliced(false);
        composition.addDefinition("def1", "-P");
        composition.addDefinition("def2", "2");
        return composition;
    }

}