package org.ringingmaster.engine.compiler;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Ignore;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.analyser.Analyser;
import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.ringingmaster.engine.composition.Composition;
import org.ringingmaster.engine.composition.ObservableComposition;
import org.ringingmaster.engine.composition.TableType;
import org.ringingmaster.engine.composition.compositiontype.CompositionType;
import org.ringingmaster.engine.helper.PlainCourseHelper;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.method.MethodBuilder;
import org.ringingmaster.engine.method.Row;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;
import org.ringingmaster.engine.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ringingmaster.engine.compiler.CompileTerminationReason.EMPTY_PARTS;

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
		final Notation allChange = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("-")
				.build();

		ObservableComposition composition = new ObservableComposition();
		composition.addNotation(allChange);
		composition.setCheckingType(CompositionType.LEAD_BASED);
		composition.setNumberOfBells(NumberOfBells.BELLS_8);
		composition.setTerminationMaxLeads(1);

		CompiledComposition compiledComposition = parser
				.andThen(compiler)
				.apply(composition.get());

		Method method = compiledComposition.getMethod().get();

		assertNotNull("Should return non null Method", method);
		assertEquals("X should produce an initial rounds row, and a single changed row", 2, method.getLead(0).getRowCount());
		assertEquals("Row 0 should be rounds", "12345678", method.getLead(0).getRow(0).getDisplayString(false));
		assertEquals("Row 1 should be all change", "21436587", method.getLead(0).getRow(1).getDisplayString(false));
	}



	@Test
	public void canConstructChangePlaceLead() {
		final Notation notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("14")
				.build();

		ObservableComposition composition = new ObservableComposition();
		composition.setCheckingType(CompositionType.LEAD_BASED);
		composition.addNotation(notation);
		composition.setTerminationMaxLeads(1);

		Method method = parser
				.andThen(compiler)
				.apply(composition.get())
				.getMethod().get();


		assertEquals("14 should produce an initial rounds row, and a single changed row", 2, method.getLead(0).getRowCount());
		assertEquals("Row 0 should be rounds", "12345678", method.getLead(0).getRow(0).getDisplayString(false));
		assertEquals("Row 1 should have place 1 & 4 made", "13246587", method.getLead(0).getRow(1).getDisplayString(false));
	}

	@Ignore
	@Test
	public void leadSeparatorPositionsCorrect() {
//		final Notation mockedNotationBody = mockNotation(NumberOfBells.BELLS_8,
//				NotationRowHelper.buildNotationRow(Place.ALL_CHANGE),
//				NotationRowHelper.buildNotationRow(Place.PLACE_1, Place.PLACE_4));
//
//		Composition composition = CompositionBuilder.buildPlainCourseComposition(mockedNotationBody);
//		composition.setTerminationMaxLeads(1);
//		CompiledComposition result = new LeadBasedCompiler(composition).compile(false, () -> false);
//		Method method = result.getMethod().get();
//
//		assertArrayEquals(new int[]{1}, method.getLead(0).getLeadSeparatorPositions());
	}

	@Test
	public void leadCountTerminationCorrectlyCountsLeads() {

		final Notation notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("x.14.x")
				.build();

		ObservableComposition composition = new ObservableComposition();
		composition.setCheckingType(CompositionType.LEAD_BASED);
		composition.addNotation(notation);
		composition.removeTerminationChange();


		for (int i=1; i< 10; i++) {

			composition.setTerminationMaxLeads(i);

			CompiledComposition compiledComposition = parser
					.andThen(compiler)
					.apply(composition.get());

			assertEquals(i, compiledComposition.getMethod().get().getLeadCount());
			assertEquals(CompileTerminationReason.LEAD_COUNT, compiledComposition.getTerminationReason());
		}
	}



	@Test
	public void rowCountTerminationCorrectlyCountsRows() {

		final Notation notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_8)
				.setUnfoldedNotationShorthand("x.14.x.14")
				.build();

		ObservableComposition composition = new ObservableComposition();
		composition.setCheckingType(CompositionType.LEAD_BASED);
		composition.addNotation(notation);
		composition.removeTerminationChange();

		for (int i=1; i< 50; i++) {
			composition.setTerminationMaxRows(i);

			CompiledComposition compiledComposition = parser
					.andThen(compiler)
					.apply(composition.get());


			Method method = compiledComposition.getMethod().get();

			assertEquals(i, method.getRowCount());
			assertEquals(CompileTerminationReason.ROW_COUNT, compiledComposition.getTerminationReason());
		}
	}

	@Test
	public void rowTerminationCorrectlyStopsAtRounds() {

		final Notation notation = NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setUnfoldedNotationShorthand("x.16.x.16")
				.build();

		ObservableComposition composition = new ObservableComposition();
		composition.setCheckingType(CompositionType.LEAD_BASED);
		composition.addNotation(notation);
		final Row roundsRow = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6);
		composition.setTerminationChange(roundsRow);

		CompiledComposition compiledComposition = parser
				.andThen(compiler)
				.apply(composition.get());

		Method method = compiledComposition.getMethod().get();

		assertEquals(roundsRow, method.getLastRow().get());
	}

	@Test
	public void compilePlainCourseOfPlainBobMinor() throws IOException {

		Composition composition = PlainCourseHelper.buildPlainCourseComposition.apply(buildPlainBobMinor());

		CompiledComposition compiledComposition = parser
				.andThen(compiler)
				.apply(composition);


		assertTrue( compiledComposition.getComposition().getTitle().startsWith(" Plain Bob Minor: PLAINCOURSE"));
		assertEquals(60, compiledComposition.getMethod().get().getRowCount());
		assertEquals(5, compiledComposition.getMethod().get().getLeadCount());
		checkAgainstFile(compiledComposition.getMethod().get(), "/PlainBobMinor.txt");
	}

	@Test
	public void compilingCompositionWithNoNotationTerminatesWithError() {

		ObservableComposition composition = null;
		try {
			composition = new ObservableComposition();
			composition.setCheckingType(CompositionType.LEAD_BASED);
			composition.setTerminationMaxRows(10);
		} catch (Exception e) {
			fail();
		}

		CompiledComposition compiledComposition = parser
				.andThen(compiler)
				.apply(composition.get());

		assertEquals(CompileTerminationReason.INVALID_COMPOSITION, compiledComposition.getTerminationReason());
		assertEquals("No active method", compiledComposition.getTerminateReasonDisplayString());
	}

	@Test
	public void compileSingleCall() throws IOException {
		checkSimple1CellPlainBobComposition("-", 3, "/PlainBobMinor - .txt", true);
	}

	@Test
	public void compileCallMultiplier() throws IOException {
		checkSimple1CellPlainBobComposition("2-", 3, "/PlainBobMinor - .txt", true);
	}

	@Test
	public void compileDefaultCallMultiplier() throws IOException {
		checkSimple1CellPlainBobComposition("2", 3, "/PlainBobMinor - .txt", true);
	}

	@Test
	public void compileCallAndPlain() throws IOException {
		checkSimple1CellPlainBobComposition("-p", 10, "/PlainBobMinor -p .txt", true);
	}

	@Test
	public void compileCallAndPlainWithWhitespace() throws IOException {
		checkSimple1CellPlainBobComposition("- p", 10, "/PlainBobMinor -p .txt", true);
	}

	@Test
	public void compileCallAndPlainMultiplier() throws IOException {
		checkSimple1CellPlainBobComposition("-2p", 6, "/PlainBobMinor -2p .txt", true);
	}

	@Test
	public void compileGroup() throws IOException {
		checkSimple1CellPlainBobComposition("2(-p)s", 30, "/PlainBobMinor 2(-p)s .txt", false);
	}

	@Test
	public void compileEmbeddedGroup() throws IOException {
		checkSimple1CellPlainBobComposition("2(2(-p)s)-", 22, "/PlainBobMinor 2(2(-p)s) .txt", false);
	}

	@Test
	public void compileOmitParts() throws IOException {
		ObservableComposition composition = buildPlainBobMinorCompositionShell();
		composition.addCharacters(TableType.MAIN_TABLE, 0, 0, "-[-2s]");
		proveAndCheckCompositionn(6, "/PlainBobMinor -[s] omit2.txt", true, CompileTerminationReason.SPECIFIED_ROW, composition.get());
	}

	@Test
	public void compileEmptyPartsTerminatedWithEmptyParts() throws IOException {
		ObservableComposition composition = buildPlainBobMinorCompositionShell();

		composition.addCharacters(TableType.MAIN_TABLE, 0, 0, "[-1,2,3-s]");

		proveAndCheckCompositionn(0, "/PlainBobMinor [-s] omit1_2_3.txt", true, EMPTY_PARTS, composition.get());
	}

	@Test
	public void compileDefinitionWithGroup() throws IOException {
		ObservableComposition composition = buildPlainBobMinorCompositionShell();
		composition.addCharacters(TableType.MAIN_TABLE, 0, 0, "-def-");
		composition.addDefinition("def", "2(sBob)");

		proveAndCheckCompositionn(6, "/PlainBobMinor -def-.txt", false, CompileTerminationReason.SPECIFIED_ROW, composition.get());
	}

	Proof checkSimple1CellPlainBobComposition(String compositionString, int expectedLeadCount, String fileName, boolean trueComposition) throws IOException {
		ObservableComposition composition = buildPlainBobMinorCompositionShell();
		composition.addCharacters(TableType.MAIN_TABLE, 0, 0, compositionString);

		return proveAndCheckCompositionn(expectedLeadCount, fileName, trueComposition, CompileTerminationReason.SPECIFIED_ROW, composition.get());
	}

	private ObservableComposition buildPlainBobMinorCompositionShell() {
		ObservableComposition composition = new ObservableComposition();
		composition.addNotation(buildPlainBobMinor());
		composition.setCheckingType(CompositionType.LEAD_BASED);
		composition.setTerminationChange(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6));
		composition.setPlainLeadToken("p");
		return composition;
	}

	private Proof proveAndCheckCompositionn(int expectedLeadCount, String fileName, boolean trueComposition,
											CompileTerminationReason terminationReason, Composition composition) throws IOException {

		Proof proof = parser.andThen(compiler).andThen(analyser).apply(composition);
		CompiledComposition compiledComposition = proof.getCompiledComposition();
		assertEquals(terminationReason, compiledComposition.getTerminationReason());
		assertEquals(expectedLeadCount, compiledComposition.getMethod().get().getLeadCount());
		checkAgainstFile(compiledComposition.getMethod().get(), fileName);
		assertEquals(trueComposition, proof.isTrueComposition());
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

	private Notation buildPlainBobMinor() {
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
