package org.ringingmaster.engine.compilernew.impl;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Ignore;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.compilernew.CompileTerminationReason;
import org.ringingmaster.engine.compilernew.Compiler;
import org.ringingmaster.engine.compilernew.proof.Proof;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.TableType;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
		final NotationBody allChange = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.build();

		ObservableTouch touch = new ObservableTouch();
		touch.addNotation(allChange);
		touch.setCheckingType(CheckingType.LEAD_BASED);
		touch.setNumberOfBells(NumberOfBells.BELLS_8);
		touch.setTerminationMaxLeads(1);

		Proof proof = parser
				.andThen(compiler)
				.apply(touch.get());

		Method method = proof.getMethod().get();

		assertNotNull("Should return non null Method", method);
		assertEquals("X should produce an initial rounds row, and a single changed row", 2, method.getLead(0).getRowCount());
		assertEquals("Row 0 should be rounds", "12345678", method.getLead(0).getRow(0).getDisplayString(false));
		assertEquals("Row 1 should be all change", "21436587", method.getLead(0).getRow(1).getDisplayString(false));
	}



	@Test
	public void canConstructChangePlaceLead() {
		final NotationBody notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("14")
				.build();

		ObservableTouch touch = new ObservableTouch();
		touch.setCheckingType(CheckingType.LEAD_BASED);
		touch.addNotation(notation);
		touch.setTerminationMaxLeads(1);

		Method method = parser
				.andThen(compiler)
				.apply(touch.get())
				.getMethod().get();


		assertEquals("14 should produce an initial rounds row, and a single changed row", 2, method.getLead(0).getRowCount());
		assertEquals("Row 0 should be rounds", "12345678", method.getLead(0).getRow(0).getDisplayString(false));
		assertEquals("Row 1 should have place 1 & 4 made", "13246587", method.getLead(0).getRow(1).getDisplayString(false));
	}

	@Ignore
	@Test
	public void leadSeparatorPositionsCorrect() {
//		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8,
//				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
//				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4));
//
//		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
//		touch.setTerminationMaxLeads(1);
//		Proof result = new LeadBasedCompiler(touch).compile(false, () -> false);
//		Method method = result.getMethod().get();
//
//		assertArrayEquals(new int[]{1}, method.getLead(0).getLeadSeparatorPositions());
	}

	@Test
	public void leadCountTerminationCorrectlyCountsLeads() {

		final NotationBody notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("x.14.x")
				.build();

		ObservableTouch touch = new ObservableTouch();
		touch.setCheckingType(CheckingType.LEAD_BASED);
		touch.addNotation(notation);
		touch.removeTerminationChange();


		for (int i=1; i< 10; i++) {

			touch.setTerminationMaxLeads(i);

			Proof proof = parser
					.andThen(compiler)
					.apply(touch.get());

			assertEquals(i, proof.getMethod().get().getLeadCount());
			assertEquals(CompileTerminationReason.LEAD_COUNT, proof.getTerminationReason());
		}
	}



	@Test
	public void rowCountTerminationCorrectlyCountsRows() {

		final NotationBody notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("x.14.x.14")
				.build();

		ObservableTouch touch = new ObservableTouch();
		touch.setCheckingType(CheckingType.LEAD_BASED);
		touch.addNotation(notation);
		touch.removeTerminationChange();

		for (int i=1; i< 50; i++) {
			touch.setTerminationMaxRows(i);

			Proof proof = parser
					.andThen(compiler)
					.apply(touch.get());


			Method method = proof.getMethod().get();

			assertEquals(i, method.getRowCount());
			assertEquals(CompileTerminationReason.ROW_COUNT, proof.getTerminationReason());
		}
	}

	@Test
	public void rowTerminationCorrectlyStopsAtRounds() {

		final NotationBody notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setUnfoldedNotationShorthand("x.16.x.16")
				.build();

		ObservableTouch touch = new ObservableTouch();
		touch.setCheckingType(CheckingType.LEAD_BASED);
		touch.addNotation(notation);
		final MethodRow roundsRow = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6);
		touch.setTerminationChange(roundsRow);

		Proof proof = parser
				.andThen(compiler)
				.apply(touch.get());

		Method method = proof.getMethod().get();

		assertEquals(roundsRow, method.getLastRow());
	}

	@Test
	public void compilePlainCourseOfPlainBobMinor() throws IOException {

		Touch touch = PlainCourseHelper.buildPlainCourseInstance.apply(buildPlainBobMinor());

		Proof proof = parser
				.andThen(compiler)
				.apply(touch);


		assertTrue( proof.getParse().getUnderlyingTouch().getTitle().startsWith("PLAINCOURSE_"));
		assertTrue( proof.getParse().getUnderlyingTouch().getTitle().endsWith(":Plain Bob Minor"));
		assertEquals(60, proof.getMethod().get().getRowCount());
		assertEquals(5, proof.getMethod().get().getLeadCount());
		checkAgainstFile(proof.getMethod().get(), "/PlainBobMinor.txt");
	}

	@Test
	public void compilingTouchWithNoNotationTerminatesWithError() {

		ObservableTouch touch = null;
		try {
			touch = new ObservableTouch();
			touch.setCheckingType(CheckingType.LEAD_BASED);
			touch.setTerminationMaxRows(10);
		} catch (Exception e) {
			fail();
		}

		Proof proof = parser
				.andThen(compiler)
				.apply(touch.get());

		assertEquals(CompileTerminationReason.INVALID_TOUCH, proof.getTerminationReason());
		assertEquals("No active method", proof.getTerminateReasonDisplayString());
	}

	@Test
	public void compileSingleCall() throws IOException {
		checkSimple1CellPlainBobTouch("-", 3, "/PlainBobMinor - .txt", true);
	}

	@Test
	public void compileCallMultiplier() throws IOException {
		checkSimple1CellPlainBobTouch("2-", 3, "/PlainBobMinor - .txt", true);
	}

	@Test
	public void compileCallAndPlain() throws IOException {
		checkSimple1CellPlainBobTouch("-p", 10, "/PlainBobMinor -p .txt", true);
	}

	@Test
	public void compileCallAndPlainMultiplier() throws IOException {
		checkSimple1CellPlainBobTouch("-2p", 6, "/PlainBobMinor -2p .txt", true);
	}

	@Test
	public void compileGroup() throws IOException {
		checkSimple1CellPlainBobTouch("2(-p)s", 30, "/PlainBobMinor 2(-p)s .txt", false);
	}

	@Test
	public void compileEmbeddedGroup() throws IOException {
		checkSimple1CellPlainBobTouch("2(2(-p)s)-", 22, "/PlainBobMinor 2(2(-p)s) .txt", false);
	}

	@Test
	public void compileOmitParts() throws IOException {
		fail();
//		ObservableTouch touch = buildPlainBobMinorTouchShell();
//		SpecifiedPartsVariance omitFromPart2 = new SpecifiedPartsVariance(VarianceLogicType.EXCLUDE, Sets.newHashSet(1));
//		touch.addCharacters(0, 0, 0, '-');
//		touch.insertCharacter(0, 0, 1, '[').setVariance(omitFromPart2);
//		touch.insertCharacter(0, 0, 2, 's');
//		touch.insertCharacter(0, 0, 3, ']');
//		parser.parseAndAnnotate(touch);
//		proveAndCheckTouch(6, "/PlainBobMinor -[s] omit2.txt", true, CompileTerminationReason.SPECIFIED_ROW, touch);
	}

	@Test
	public void compileEmptyPartsTerminatedWithEmptyParts() throws IOException {
		fail();
//		Touch touch = buildPlainBobMinorTouchShell();
//		SpecifiedPartsVariance omitFromPart1_2_3 = new SpecifiedPartsVariance(VarianceLogicType.EXCLUDE, Sets.newHashSet(0,1));
//		touch.insertCharacter(0, 0, 0, '[').setVariance(omitFromPart1_2_3);
//		touch.insertCharacter(0, 0, 1, '-');
//		touch.insertCharacter(0, 0, 2, 's');
//		touch.insertCharacter(0, 0, 3, ']');
//		parser.parseAndAnnotate(touch);
//		proveAndCheckTouch(0, "/PlainBobMinor [-s] omit1_2_3.txt", true, CompileTerminationReason.EMPTY_PARTS, touch);
	}

	@Test
	public void compileDefinitionWithGroup() throws IOException {
		ObservableTouch touch = buildPlainBobMinorTouchShell();
		touch.addCharacters(TableType.TOUCH_TABLE, 0, 0, "-def-");
		touch.addDefinition("def", "2(sBob)");

		proveAndCheckTouch(6, "/PlainBobMinor -def-.txt", false, CompileTerminationReason.SPECIFIED_ROW, touch.get());
	}

	Proof checkSimple1CellPlainBobTouch(String touchString, int expectedLeadCount, String fileName, boolean trueTouch) throws IOException {
		ObservableTouch touch = buildPlainBobMinorTouchShell();
		touch.addCharacters(TableType.TOUCH_TABLE, 0, 0, touchString);

		return proveAndCheckTouch(expectedLeadCount, fileName, trueTouch, CompileTerminationReason.SPECIFIED_ROW, touch.get());
	}

	private ObservableTouch buildPlainBobMinorTouchShell() {
		ObservableTouch touch = new ObservableTouch();
		touch.addNotation(buildPlainBobMinor());
		touch.setCheckingType(CheckingType.LEAD_BASED);
		touch.setTerminationChange(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6));
		touch.setPlainLeadToken("p");
		return touch;
	}

	private Proof proveAndCheckTouch(int expectedLeadCount, String fileName, boolean trueTouch,
	                                 CompileTerminationReason terminationReason, Touch touch) throws IOException {

		Proof proof = parser.andThen(compiler).apply(touch);
		assertEquals(terminationReason, proof.getTerminationReason());
		assertEquals(expectedLeadCount, proof.getMethod().get().getLeadCount());
		checkAgainstFile(proof.getMethod().get(), fileName);
		assertEquals(trueTouch, proof.getAnalysis().get().isTrueTouch());
		return proof;
	}

	private void checkAgainstFile(Method method, String fileName) throws IOException {
		String allChangesAsText = method.getAllChangesAsText();
		String fileContent;

		try (InputStream stream = getClass().getResourceAsStream(fileName)) {
			fileContent = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
		}

		assertEquals(convertToLineSeparators(fileContent), convertToLineSeparators(allChangesAsText));
	}

	private String convertToLineSeparators(String text) {
		text = text.replace("\r\n", System.lineSeparator());
		text = text.replace("\r", System.lineSeparator());
		text = text.replace("\n", System.lineSeparator());
		return text;
	}


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
