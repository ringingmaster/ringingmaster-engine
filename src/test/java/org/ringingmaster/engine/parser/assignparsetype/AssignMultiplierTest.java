package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.collect.Iterables;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.invalid;
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
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.VARIANCE_OPEN;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.WHITESPACE;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.checkingtype.CheckingType.COURSE_BASED;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignMultiplierTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), null);
        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertEquals(0, parse.allTouchCells().getRowSize());
        assertEquals(0, parse.allTouchCells().getColumnSize());
    }

    @Test
    public void parsingAllCellTypesReturnsOriginals() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), null);
        touch.setTouchCheckingType(COURSE_BASED);

        touch.addCharacters(TOUCH_TABLE, 0,0, "CALL_POSITION");
        touch.addCharacters(TOUCH_TABLE, 1,0, "MAIN_BODY");
        touch.addCharacters(TOUCH_TABLE, 1,1, "SPLICE");
        touch.addCharacters(TOUCH_TABLE, 2,0, "CALL");// To force the Parse to be replaced
        touch.addCharacters(TOUCH_TABLE, 2,1, "CALL");// To force the Parse to be replaced

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertEquals(3, parse.allTouchCells().getRowSize());
        assertEquals(2, parse.allTouchCells().getColumnSize());
        assertEquals("CALL_POSITION", parse.allTouchCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", parse.allTouchCells().get(1,0).getCharacters());
        assertEquals("SPLICE", parse.allTouchCells().get(1,1).getCharacters());
    }


    @Test
	public void correctlyParseSingleDefaultCallMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-2");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(CALL), valid(DEFAULT_CALL_MULTIPLIER));
	}

	@Test
	public void correctlyParseMultiDefaultCallMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-22");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(CALL), valid(2,DEFAULT_CALL_MULTIPLIER));
	}

    @Test
    public void correctlyParseDefaultCallMultiplierBeforeWhitespace() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "2 ");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(DEFAULT_CALL_MULTIPLIER), valid(WHITESPACE));
    }

    @Test
    public void correctlyParseDefaultCallMultiplierBeforeVariance() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "6[7]");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(DEFAULT_CALL_MULTIPLIER), valid(VARIANCE_OPEN), valid(DEFAULT_CALL_MULTIPLIER), valid(VARIANCE_CLOSE));
    }

    @Test
    public void correctlyParseCallMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "2-");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(section(CALL_MULTIPLIER), section(CALL)));
    }

    @Test
    public void correctlyParseMultiCallMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "28-");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(section(2,CALL_MULTIPLIER), section(CALL)));
    }

    @Test
    public void correctlyParseGroupMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "6(7)");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(section(MULTIPLIER_GROUP_OPEN_MULTIPLIER), section(MULTIPLIER_GROUP_OPEN)), valid(DEFAULT_CALL_MULTIPLIER), valid(MULTIPLIER_GROUP_CLOSE));
    }

    @Test
    public void correctlyParseMultiGroupMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "624(");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(section(3, MULTIPLIER_GROUP_OPEN_MULTIPLIER), section(MULTIPLIER_GROUP_OPEN)));
    }

    @Test
    public void correctlyParsePlainLeadMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "3p");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(section(PLAIN_LEAD_MULTIPLIER), section(PLAIN_LEAD)));
    }

    @Test
    public void correctlyParseMultiPlainLeadMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "434p");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(section(3, PLAIN_LEAD_MULTIPLIER), section(PLAIN_LEAD)));
    }

    @Test
    public void correctlyParseDefinitionMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "2def1");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(section(1, DEFINITION_MULTIPLIER), section(4, DEFINITION)));
    }

    @Test
    public void correctlyParseMultiDefinitionMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "243def1");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), valid(section(3, DEFINITION_MULTIPLIER), section(4, DEFINITION)));
    }

    @Test
    public void correctlyParseSpliceMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "DUMMY");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "2p");
        touch.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,1), valid(section(1, SPLICE_MULTIPLIER), section(SPLICE)));
    }

    @Test
    public void correctlyParseMultiSpliceMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "DUMMY");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "392p");
        touch.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,1), valid(section(3, SPLICE_MULTIPLIER), section(SPLICE)));
    }

    @Test
    public void standAloneNumbersInSplicedUnparsed() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "DUMMY");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "392-");
        touch.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,1), unparsed(4));
    }

    @Test
    public void defaultCallNotDefinedInChosenMethodSetsInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "2");
        touch.removeNotation(Iterables.getOnlyElement(touch.get().getAllNotations()));

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), invalid(1, DEFAULT_CALL_MULTIPLIER, "No default call defined"));
    }

    @Test
    public void defaultCallNotDefinedInAllMethodsInSplicedSetsInvalid() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "2");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "DUMMY");
        touch.addNotation(buildLittleBobMinorWithNoDefaultCall());
        touch.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.allTouchCells().get(0,0), invalid(DEFAULT_CALL_MULTIPLIER));
    }


    @Test
    public void multiplierWorksInDefinition() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def2");

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def2").get().get(0,1), valid(DEFAULT_CALL_MULTIPLIER));
    }

    @Test
    public void multiplierDoesNotAddDefaultCallWhenUsedOnlyInSplice() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "def2");
        touch.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def2").get().get(0,DEFINITION_COLUMN), unparsed());
    }

    @Test
    public void multiplierDoesAddDefaultCallWhenUsedInMainCells() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def2");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "-");
        touch.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def2").get().get(0,DEFINITION_COLUMN), valid(DEFAULT_CALL_MULTIPLIER));
    }

    @Test
    public void multiplierDoesNotAddDefaultCallWhenUsedOnlyInSpliceAndMainCells() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "def2");
        touch.addCharacters(TOUCH_TABLE, 0, 1, "def2");
        touch.setSpliced(true);

        Parse parse = new AssignParseType()
                .andThen(new AssignMultiplier())
                .apply(touch.get());

        assertParse(parse.findDefinitionByShorthand("def2").get().get(0, DEFINITION_COLUMN), unparsed());
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

    private ObservableTouch buildSingleCellTouch(NotationBody notationBody, String characters) {
        ObservableTouch touch = new ObservableTouch();
        touch.setNumberOfBells(notationBody.getNumberOfWorkingBells());
        if (characters != null) {
            touch.addCharacters(TOUCH_TABLE, 0, 0, characters);
        }
        touch.addNotation(notationBody);
        touch.setTouchCheckingType(CheckingType.LEAD_BASED);
        touch.setSpliced(false);
        touch.addDefinition("def1", "-P");
        touch.addDefinition("def2", "2");
        return touch;
    }

}