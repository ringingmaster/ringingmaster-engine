package org.ringingmaster.engine.parser.assignparse;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.Parse;
import org.ringingmaster.engine.touch.container.ObservableTouch;
import org.ringingmaster.engine.touch.container.checkingtype.CheckingType;

import static org.junit.Assert.assertEquals;
import static org.ringingmaster.engine.parser.AssertParse.assertParse;
import static org.ringingmaster.engine.parser.AssertParse.valid;
import static org.ringingmaster.engine.parser.ParseType.CALL;
import static org.ringingmaster.engine.parser.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.touch.container.TableType.TOUCH_TABLE;
import static org.ringingmaster.engine.touch.container.checkingtype.CheckingType.COURSE_BASED;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignMultiplierTest {

    @Test
    public void parsingEmptyParseReturnsEmptyParse() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), null);
        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new AssignMultiplier().apply(parse);

        assertEquals(0, result.allTouchCells().getRowSize());
        assertEquals(0, result.allTouchCells().getColumnSize());
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

        Parse parse = new AssignParseType().apply(touch.get());
        Parse result = new AssignMultiplier().apply(parse);

        assertEquals(3, result.allTouchCells().getRowSize());
        assertEquals(2, result.allTouchCells().getColumnSize());
        assertEquals("CALL_POSITION", result.allTouchCells().get(0,0).getCharacters());
        assertEquals("MAIN_BODY", result.allTouchCells().get(1,0).getCharacters());
        assertEquals("SPLICE", result.allTouchCells().get(1,1).getCharacters());
    }


    @Test
	public void correctlyParseSingleDefaultCallMultiplier() {
        ObservableTouch touch = buildSingleCellTouch(buildPlainBobMinor(), "-2");
        Parse parse = new AssignParseType().apply(touch.get());
        assertParse(parse.allTouchCells().get(0,0), valid(CALL), valid(CALL_MULTIPLIER));
	}
//TODO
//	@Test
//	public void correctlyParseMultiDefaultCallMultiplier() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "s22");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL, ParseType.DEFAULT_CALL_MULTIPLIER, ParseType.DEFAULT_CALL_MULTIPLIER);
//	}
//
//	@Test
//	public void correctlyParseDefaultCallMultiplierBeforeWhitespace() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "2 ");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.DEFAULT_CALL_MULTIPLIER, ParseType.WHITESPACE);
//	}
//
//	@Test
//	public void correctlyParseDefaultCallMultiplierBeforeVariance() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "6[7]");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.DEFAULT_CALL_MULTIPLIER, ParseType.VARIANCE_OPEN, ParseType.DEFAULT_CALL_MULTIPLIER, ParseType.VARIANCE_CLOSE);
//	}
//
//	@Test
//	public void correctlyParseCallMultiplier() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "2-");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL_MULTIPLIER, ParseType.CALL);
//	}
//
//	@Test
//	public void correctlyParseMultiCallMultiplier() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "28-");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL_MULTIPLIER, ParseType.CALL_MULTIPLIER, ParseType.CALL);
//	}
//
//	@Test
//	public void correctlyParseGroupMultiplier() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "6(7)");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.GROUP_OPEN_MULTIPLIER, ParseType.GROUP_OPEN, ParseType.DEFAULT_CALL_MULTIPLIER, ParseType.GROUP_CLOSE);
//	}
//
//	@Test
//	public void correctlyParseMultiGroupMultiplier() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "64(");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.GROUP_OPEN_MULTIPLIER, ParseType.GROUP_OPEN_MULTIPLIER, ParseType.GROUP_OPEN);
//	}
//
//	@Test
//	public void correctlyParsePlainLeadMultiplier() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "3p");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.PLAIN_LEAD_MULTIPLIER, ParseType.PLAIN_LEAD);
//	}
//
//	@Test
//	public void correctlyParseMultiPlainLeadMultiplier() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "34p");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.PLAIN_LEAD_MULTIPLIER, ParseType.PLAIN_LEAD_MULTIPLIER, ParseType.PLAIN_LEAD);
//	}
//
//	@Test
//	public void correctlyParseDefinitionMultiplier() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		touch.addCharacters(0, 0, "2z");
//		touch.addNotation(buildPlainBobMinor());
//		touch.addDefinition("z", "-s");
//		new DefaultParser().parseAndAnnotate(touch);
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.DEFINITION_MULTIPLIER, ParseType.DEFINITION);
//	}
//
//	@Test
//	public void correctlyParseMultiDefinitionMultiplier() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		touch.addCharacters(0, 0, "92z");
//		touch.addNotation(buildPlainBobMinor());
//		touch.addDefinition("z", "-s");
//		new DefaultParser().parseAndAnnotate(touch);
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.DEFINITION_MULTIPLIER, ParseType.DEFINITION_MULTIPLIER, ParseType.DEFINITION);
//	}
//
//	@Test
//	public void correctlyParseSpliceMultiplier() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		touch.addCharacters(0, 0, "2p");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setSpliced(true);
//		new DefaultParser().parseAndAnnotate(touch);
//
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.SPLICE_MULTIPLIER, ParseType.SPLICE);
//	}
//
//	@Test
//	public void correctlyParseMultiSpliceMultiplier() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		touch.addCharacters(0, 0, "392p");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setSpliced(true);
//		new DefaultParser().parseAndAnnotate(touch);
//
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.SPLICE_MULTIPLIER, ParseType.SPLICE_MULTIPLIER, ParseType.SPLICE_MULTIPLIER, ParseType.SPLICE);
//	}


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
                .setSpliceIdentifier("P")
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
        return touch;
    }

}