package org.ringingmaster.engine.compiler;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Ignore;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.analyser.Analyser;
import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.compiler.compiledtouch.CompiledTouch;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Row;
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
import static org.ringingmaster.engine.compiler.CompileTerminationReason.*;

/**
 * User: Stephen
 */
public class LeadBasedCompilerTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Parser parser = new Parser();
	private static Compiler compiler = new Compiler();
	private static Analyser analyser = new Analyser();

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

		CompiledTouch compiledTouch = parser
				.andThen(compiler)
				.apply(touch.get());

		Method method = compiledTouch.getMethod().get();

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
//		CompiledTouch result = new LeadBasedCompiler(touch).compile(false, () -> false);
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

			CompiledTouch compiledTouch = parser
					.andThen(compiler)
					.apply(touch.get());

			assertEquals(i, compiledTouch.getMethod().get().getLeadCount());
			assertEquals(CompileTerminationReason.LEAD_COUNT, compiledTouch.getTerminationReason());
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

			CompiledTouch compiledTouch = parser
					.andThen(compiler)
					.apply(touch.get());


			Method method = compiledTouch.getMethod().get();

			assertEquals(i, method.getRowCount());
			assertEquals(CompileTerminationReason.ROW_COUNT, compiledTouch.getTerminationReason());
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
		final Row roundsRow = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6);
		touch.setTerminationChange(roundsRow);

		CompiledTouch compiledTouch = parser
				.andThen(compiler)
				.apply(touch.get());

		Method method = compiledTouch.getMethod().get();

		assertEquals(roundsRow, method.getLastRow().get());
	}

	@Test
	public void compilePlainCourseOfPlainBobMinor() throws IOException {

		Touch touch = PlainCourseHelper.buildPlainCourseInstance.apply(buildPlainBobMinor());

		CompiledTouch compiledTouch = parser
				.andThen(compiler)
				.apply(touch);


		assertTrue( compiledTouch.getTouch().getTitle().startsWith("PLAINCOURSE_"));
		assertTrue( compiledTouch.getTouch().getTitle().endsWith(":Plain Bob Minor"));
		assertEquals(60, compiledTouch.getMethod().get().getRowCount());
		assertEquals(5, compiledTouch.getMethod().get().getLeadCount());
		checkAgainstFile(compiledTouch.getMethod().get(), "/PlainBobMinor.txt");
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

		CompiledTouch compiledTouch = parser
				.andThen(compiler)
				.apply(touch.get());

		assertEquals(CompileTerminationReason.INVALID_TOUCH, compiledTouch.getTerminationReason());
		assertEquals("No active method", compiledTouch.getTerminateReasonDisplayString());
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
		ObservableTouch touch = buildPlainBobMinorTouchShell();
		touch.addCharacters(TableType.TOUCH_TABLE, 0, 0, "-[-2s]");
		proveAndCheckTouch(6, "/PlainBobMinor -[s] omit2.txt", true, CompileTerminationReason.SPECIFIED_ROW, touch.get());
	}

	@Test
	public void compileEmptyPartsTerminatedWithEmptyParts() throws IOException {
		ObservableTouch touch = buildPlainBobMinorTouchShell();

		touch.addCharacters(TableType.TOUCH_TABLE, 0, 0, "[-1,2,3-s]");

		proveAndCheckTouch(0, "/PlainBobMinor [-s] omit1_2_3.txt", true, EMPTY_PARTS, touch.get());
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

		Proof proof = parser.andThen(compiler).andThen(analyser).apply(touch);
		CompiledTouch compiledTouch = proof.getCompiledTouch();
		assertEquals(terminationReason, compiledTouch.getTerminationReason());
		assertEquals(expectedLeadCount, compiledTouch.getMethod().get().getLeadCount());
		checkAgainstFile(compiledTouch.getMethod().get(), fileName);
		assertEquals(trueTouch, proof.isTrueTouch());
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
