package org.ringingmaster.engine.parser.impl;

import org.junit.Test;

/**
 * User: Stephen
 */
public class DefaultTouchParserTest {


	@Test
	public void fail() {
		fail();
		//TODO
	}

//	@Test
//	public void correctlyParsesSimpleWhitespace() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "- Bob");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL, ParseType.WHITESPACE, ParseType.CALL, ParseType.CALL, ParseType.CALL);
//	}
//
//	@Test
//	public void correctlyParsesPlainLeadToken() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "-p-");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL, ParseType.PLAIN_LEAD, ParseType.CALL);
//	}
//
//	@Test
//	public void correctlyParsesSpliceToken() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		touch.addCharacters(0, 0, "-p-");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setSpliced(true);
//		new DefaultParser().parseAndAnnotate(touch);
//
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.UNPARSED, ParseType.SPLICE, ParseType.UNPARSED);
//	}
//
//	@Test
//	public void correctlyParsesSimpleCallPosition() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.COURSE_BASED);
//		touch.addCharacters(0, 0, "W");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setSpliced(false);
//		new DefaultParser().parseAndAnnotate(touch);
//
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALLING_POSITION);
//	}
//
//	@Test
//	public void ignoreSecondCallingPositionInCell() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.COURSE_BASED);
//		touch.addCharacters(0, 0, "HW");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setSpliced(false);
//		new DefaultParser().parseAndAnnotate(touch);
//
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALLING_POSITION, ParseType.CALLING_POSITION);
//		assertValid(touch.getCell_FOR_TEST_ONLY(0, 0), true, false);
//	}
//
//	@Test
//	public void ignoreOtherStuffInCallingPositionCell() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.COURSE_BASED);
//		touch.addCharacters(0, 0, "bHd");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setSpliced(false);
//		new DefaultParser().parseAndAnnotate(touch);
//
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.UNPARSED, ParseType.CALLING_POSITION, ParseType.UNPARSED);
//		assertValid(touch.getCell_FOR_TEST_ONLY(0, 0), false, true, false);
//	}
//
//
//	@Test
//	public void correctlyIgnoresSpliceTokenWhenNotSpliced() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.addCharacters(0, 0, "-p-");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setSpliced(false);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		new DefaultParser().parseAndAnnotate(touch);
//
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL, ParseType.PLAIN_LEAD, ParseType.CALL);
//	}
//
//	@Test
//	public void correctlyParsesDefinitionToken() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		touch.addCharacters(0, 0, "-z-");
//		touch.addNotation(buildPlainBobMinor());
//		touch.addDefinition("z", "-s");
//		new DefaultParser().parseAndAnnotate(touch);
//
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL, ParseType.DEFINITION, ParseType.CALL);
//	}
//
//	// TODO make sure a definition is not in use in both spliced and main body.
//
//	@Test
//	public void correctlyAllocatedOverlappingParsings() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		touch.addCharacters(0, 0, "Bob");
//		touch.setPlainLeadToken("b"); // the same as the last 'b' in Bob
//		touch.addNotation(buildPlainBobMinor());
//		new DefaultParser().parseAndAnnotate(touch);
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL, ParseType.CALL, ParseType.CALL);
//	}
//
//	@Test
//	public void correctlyAllocatedAdjacentParsings() {
//		DefaultTouch touch = (DefaultTouch) TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
//		touch.addCharacters(0, 0, "Bobb");
//		touch.setPlainLeadToken("b"); // the same as the last 'b' in Bob
//		touch.addNotation(buildPlainBobMinor());
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		new DefaultParser().parseAndAnnotate(touch);
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL, ParseType.CALL, ParseType.CALL, ParseType.PLAIN_LEAD);
//	}
//
//	@Test
//	public void correctlyIdentifiesGroup() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "(-)s");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.GROUP_OPEN, ParseType.CALL, ParseType.GROUP_CLOSE, ParseType.CALL);
//	}
//
//	@Test
//	public void correctlyIdentifiesEmbeddedGroup() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "((-)s)");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.GROUP_OPEN, ParseType.GROUP_OPEN, ParseType.CALL, ParseType.GROUP_CLOSE, ParseType.CALL, ParseType.GROUP_CLOSE);
//		assertValid(touch.getCell_FOR_TEST_ONLY(0, 0), true, true, true, true, true, true);
//	}
//
//	@Test
//	public void invalidatesStartBraceIfNoEndBrace() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "(-");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.GROUP_OPEN, ParseType.CALL);
//		assertValid(touch.getCell_FOR_TEST_ONLY(0, 0), false, true);
//	}
//
//	@Test
//	public void invalidatesStartBraceIfNoEndBraceAndEmbeddedGroup() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "(s2(-)");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.GROUP_OPEN, ParseType.CALL, ParseType.GROUP_OPEN_MULTIPLIER, ParseType.GROUP_OPEN, ParseType.CALL, ParseType.GROUP_CLOSE);
//		assertValid(touch.getCell_FOR_TEST_ONLY(0, 0), false, true, true, true, true, true);
//	}
//
//	@Test
//	public void correctlyIdentifiesVariance() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "[-]s");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.VARIANCE_OPEN, ParseType.CALL, ParseType.VARIANCE_CLOSE, ParseType.CALL);
//	}
//
//	@Test
//	public void correctlyParseSingleDefaultCallMultiplier() {
//		DefaultTouch touch = buildAndParseSingleCellTouch(buildPlainBobMinor(), "-2");
//		assertParseType(touch.getCell_FOR_TEST_ONLY(0, 0), ParseType.CALL, ParseType.DEFAULT_CALL_MULTIPLIER);
//	}
//
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
//
//	private DefaultTouch buildAndParseSingleCellTouch(NotationBody notationBody, String characters) {
//		Touch touch = TouchBuilder.newTouch(notationBody.getNumberOfWorkingBells(), 1, 1);
//		touch.addCharacters(0, 0, characters);
//		touch.addNotation(notationBody);
//		touch.setTouchCheckingType(CheckingType.LEAD_BASED);
//		new DefaultParser().parseAndAnnotate(touch);
//		return (DefaultTouch) touch;
//	}
//
//	private NotationBody buildPlainBobMinor() {
//
//		return NotationBuilder.getInstance()
//				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
//				.setName("Plain Bob")
//				.setFoldedPalindromeNotationShorthand("x16x16x16", "12")
//				.addCall("Bob", "-", "14", true)
//				.addCall("Single", "s", "1234", false)
//				.addCallInitiationRow(7)
//				.addMethodCallingPosition("W", 7, 1)
//				.addMethodCallingPosition("H", 7, 2)
//				.setSpliceIdentifier("p")
//				.build();
//	}
//
//	private void assertParseType(TouchCell cell, ParseType... parseResult) {
//		assertEquals(cell.getLength(), parseResult.length);
//		for (int i=0;i<cell.getLength();i++) {
//			Assert.assertEquals("parse type index " + i, parseResult[i], cell.getElement(i).getParseType());
//		}
//	}
//
//	private void assertValid(TouchCell cell, boolean... valid) {
//		assertEquals(cell.getLength(), valid.length);
//		for (int i=0;i<cell.getLength();i++) {
//			if (cell.getElement(i).getWord() == null) {
//				assertEquals("valid index " + i, valid[i], false);
//			}
//			else {
//				assertEquals("valid index " + i, valid[i], cell.getElement(i).getWord().isValid());
//			}
//		}
//	}
}
