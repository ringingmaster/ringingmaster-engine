package org.ringingmaster.engine.compiler.impl;

import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.compilernew.Compiler;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * User: Stephen
 */
public class LeadBasedCompilerTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Parser parser = new Parser();
	private static Compiler compiler = new Compiler();

	@Test
	public void canConstructAllChangeLeadWithNoCalls() {
		final NotationBody mockedNotationBody = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.build();
		ObservableTouch touch = new ObservableTouch();
		touch.addNotation(mockedNotationBody);
//		touch.setTerminationMaxLeads(1);
		touch.setCheckingType(CheckingType.LEAD_BASED);
		touch.setNumberOfBells(NumberOfBells.BELLS_8);

		Proof proof = parser
				.andThen(compiler)
				.apply(touch.get());

		Method method = proof.getCreatedMethod().get();

		assertNotNull("Should return non null Method", method);
		assertEquals("X should produce an initial rounds row, and a single changed row", 2, method.getLead(0).getRowCount());
		assertEquals("Row 0 should be rounds", "12345678", method.getLead(0).getRow(0).getDisplayString(false));
		assertEquals("Row 1 should be all change", "21436587", method.getLead(0).getRow(1).getDisplayString(false));
	}

	@Test
	public void failTest() {
		fail(); //TODO

	}
//

	//	@Test
//	public void canConstructChangePlaceLead() {
//		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8, NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4));
//
//		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
//		touch.setTerminationMaxLeads(1);
//		Proof result = new LeadBasedCompiler(touch).compile(false, () -> false);
//		Method method = result.getCreatedMethod().get();
//
//		assertEquals("14 should produce an initial rounds row, and a single changed row", 2, method.getLead(0).getRowCount());
//		assertEquals("Row 0 should be rounds", "12345678", method.getLead(0).getRow(0).getDisplayString(false));
//		assertEquals("Row 1 should have place 1 & 4 made", "13246587", method.getLead(0).getRow(1).getDisplayString(false));
//	}
//
//	@Ignore
//	@Test
//	public void leadSeparatorPositionsCorrect() {
//		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8,
//				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
//				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4));
//
//		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
//		touch.setTerminationMaxLeads(1);
//		Proof result = new LeadBasedCompiler(touch).compile(false, () -> false);
//		Method method = result.getCreatedMethod().get();
//
//		assertArrayEquals(new int[]{1}, method.getLead(0).getLeadSeparatorPositions());
//	}
//
//	@Test
//	public void leadCountTerminationCorrectlyCountsLeads() {
//		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8,
//				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
//				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4),
//				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE));
//
//		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
//		touch.removeTerminationChange();
//
//		for (int i=1; i< 50; i++) {
//
//			touch.setTerminationMaxLeads(i);
//			Proof result = new LeadBasedCompiler(touch).compile(false, () -> false);
//			Method method = result.getCreatedMethod().get();
//
//			assertEquals(i, method.getLeadCount());
//			assertEquals(ProofTerminationReason.LEAD_COUNT, result.getTerminationReason());
//		}
//	}
//
//	@Test
//	public void rowCountTerminationCorrectlyCountsRows() {
//		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8,
//				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
//				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4),
//				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
//				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4));
//
//		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
//		touch.removeTerminationChange();
//
//		for (int i=1; i< 200; i++) {
//			touch.setTerminationMaxRows(i);
//			Proof result = new LeadBasedCompiler(touch).compile(false, () -> false);
//			Method method = result.getCreatedMethod().get();
//
//			assertEquals(i, method.getRowCount());
//			assertEquals(ProofTerminationReason.ROW_COUNT, result.getTerminationReason());
//		}
//	}
//
//	@Test
//	public void rowTerminationCorrectlyStopsAtRounds() {
//
//		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_6,
//				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
//				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_6),
//				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
//				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_6));
//
//		final MethodRow roundsRow = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6);
//		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
//		touch.setTerminationChange(roundsRow);
//		Proof result = new LeadBasedCompiler(touch).compile(false, () -> false);
//		Method method = result.getCreatedMethod().get();
//
//		assertEquals(roundsRow, method.getLastRow());
//	}
//
//	@Test
//	public void compilePlainCourseOfPlainBobMinor() throws IOException {
//		Touch touch = TouchBuilder.buildPlainCourseInstance(buildPlainBobMinor());
//		Proof result = new LeadBasedCompiler(touch).compile(false, () -> false);
//		assertEquals("Plain Course of Plain Bob Minor", result.getUnderlyingTouch().getTitle());
//		assertEquals(60, result.getCreatedMethod().get().getRowCount());
//		assertEquals(5, result.getCreatedMethod().get().getLeadCount());
//		checkAgainstFile(result.getCreatedMethod().get(), "/PlainBobMinor.txt");
//	}
//
//	@Test
//	public void compilingTouchWithNoNotationTerminatesWithError() {
//
//		fail(); //TODO
////		Touch touch = null;
////		try {
////			touch = TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
////			touch.setTerminationMaxRows(10);
////			touch.setCheckingType(CheckingType.LEAD_BASED);
////		} catch (Exception e) {
////			fail();
////		}
////		LeadBasedCompiler leadBasedCompiler = new LeadBasedCompiler(touch);
////		Proof compile = leadBasedCompiler.compile(false, () -> false);
////		Assert.assertEquals(ProofTerminationReason.INVALID_TOUCH, compile.getTerminationReason());
////		Assert.assertEquals("No active method", compile.getTerminateReasonDisplayString());
//	}
//
//	@Test
//	public void compileSingleCall() throws IOException {
//		checkSimple1CellPlainBobTouch("-", 3, "/PlainBobMinor - .txt", true);
//	}
//
//	@Test
//	public void compileCallMultiplier() throws IOException {
//		checkSimple1CellPlainBobTouch("2-", 3, "/PlainBobMinor - .txt", true);
//	}
//
//	@Test
//	public void compileCallAndPlain() throws IOException {
//		checkSimple1CellPlainBobTouch("-p", 10, "/PlainBobMinor -p .txt", true);
//	}
//
//	@Test
//	public void compileCallAndPlainMultiplier() throws IOException {
//		checkSimple1CellPlainBobTouch("-2p", 6, "/PlainBobMinor -2p .txt", true);
//	}
//
//	@Test
//	public void compileGroup() throws IOException {
//		checkSimple1CellPlainBobTouch("2(-p)s", 30, "/PlainBobMinor 2(-p)s .txt", false);
//	}
//
//	@Test
//	public void compileEmbeddedGroup() throws IOException {
//		checkSimple1CellPlainBobTouch("2(2(-p)s)-", 22, "/PlainBobMinor 2(2(-p)s) .txt", false);
//	}
//
//	@Test
//	public void compileOmitParts() throws IOException {
//		Touch touch = buildPlainBobMinorTouchShell();
//		SpecifiedPartsVariance omitFromPart2 = new SpecifiedPartsVariance(VarianceLogicType.EXCLUDE, Sets.newHashSet(1));
//		touch.insertCharacter(0, 0, 0, '-');
//		touch.insertCharacter(0, 0, 1, '[').setVariance(omitFromPart2);
//		touch.insertCharacter(0, 0, 2, 's');
//		touch.insertCharacter(0, 0, 3, ']');
//		parser.parseAndAnnotate(touch);
//		proveAndCheckTouch(6, "/PlainBobMinor -[s] omit2.txt", true, ProofTerminationReason.SPECIFIED_ROW, touch);
//	}
//
//	@Test
//	public void compileEmptyPartsTerminatedWithEmptyParts() throws IOException {
//		Touch touch = buildPlainBobMinorTouchShell();
//		SpecifiedPartsVariance omitFromPart1_2_3 = new SpecifiedPartsVariance(VarianceLogicType.EXCLUDE, Sets.newHashSet(0,1));
//		touch.insertCharacter(0, 0, 0, '[').setVariance(omitFromPart1_2_3);
//		touch.insertCharacter(0, 0, 1, '-');
//		touch.insertCharacter(0, 0, 2, 's');
//		touch.insertCharacter(0, 0, 3, ']');
//		parser.parseAndAnnotate(touch);
//		proveAndCheckTouch(0, "/PlainBobMinor [-s] omit1_2_3.txt", true, ProofTerminationReason.EMPTY_PARTS, touch);
//	}
//
//	@Test
//	public void compileDefinitionWithGroup() throws IOException {
//		Touch touch = buildPlainBobMinorTouchShell();
//		touch.addCharacters(0, 0, "-def-");
//		touch.addDefinition("def", "2(s-)");
//		parser.parseAndAnnotate(touch);
//		proveAndCheckTouch(6, "/PlainBobMinor -def-.txt", false, ProofTerminationReason.SPECIFIED_ROW, touch);
//	}
//
//	Proof checkSimple1CellPlainBobTouch(String touchString, int expectedLeadCount, String fileName, boolean trueTouch) throws IOException {
//		Touch touch = buildPlainBobMinorTouchShell();
//		touch.addCharacters(0, 0, touchString);
//		parser.parseAndAnnotate(touch);
//		return proveAndCheckTouch(expectedLeadCount, fileName, trueTouch, ProofTerminationReason.SPECIFIED_ROW, touch);
//	}
//
//	private Touch buildPlainBobMinorTouchShell() {
//		fail(); //TODO
//		return null;
//
////		Touch touch = TouchBuilder.newTouch(NumberOfBells.BELLS_6, 1, 1);
////		touch.addNotation(buildPlainBobMinor());
////		touch.setCheckingType(CheckingType.LEAD_BASED);
////		touch.setTerminationChange(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6));
////		touch.setPlainLeadToken("p");
////		return touch;
//	}
//
//	private Proof proveAndCheckTouch(int expectedLeadCount, String fileName, boolean trueTouch,
//	                                 ProofTerminationReason terminationReason, Touch touch) throws IOException {
//		Proof proof = new LeadBasedCompiler(touch).compile(true, () -> false);
//		assertEquals(terminationReason, proof.getTerminationReason());
//		assertEquals(expectedLeadCount, proof.getCreatedMethod().get().getLeadCount());
//		checkAgainstFile(proof.getCreatedMethod().get(), fileName);
//		assertEquals(trueTouch, proof.getAnalysis().get().isTrueTouch());
//		return proof;
//	}
//
//	private void checkAgainstFile(Method method, String fileName) throws IOException {
//		String allChangesAsText = method.getAllChangesAsText();
//		String fileContent;
//
//		try (InputStream stream = getClass().getResourceAsStream(fileName)) {
//			fileContent = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
//		}
//
//		Assert.assertEquals(convertToLineSeparators(fileContent), convertToLineSeparators(allChangesAsText));
//	}
//
//	private String convertToLineSeparators(String text) {
//		text = text.replace("\r\n", System.lineSeparator());
//		text = text.replace("\r", System.lineSeparator());
//		text = text.replace("\n", System.lineSeparator());
//		return text;
//	}
//

//	private ObservableTouch buildPlainBobMinorTouchShell() {
//		ObservableTouch touch = new ObservableTouch();
//		touch.setNumberOfBells(NumberOfBells.BELLS_6);
//		touch.setTitle("Test Touch");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setCheckingType(CheckingType.COURSE_BASED);
//		return touch;
//	}


	private NotationBody buildPlainBobMinor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Plain Bob")
				.setFoldedPalindromeNotationShorthand("-16-16-16", "12")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.build();
	}


	//TODO compound terminations


}
