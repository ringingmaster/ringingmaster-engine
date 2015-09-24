package com.concurrentperformance.ringingmaster.engine.touch.compiler.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Method;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationRowHelper;
import com.concurrentperformance.ringingmaster.engine.touch.parser.Parser;
import com.concurrentperformance.ringingmaster.engine.touch.parser.impl.DefaultParser;
import com.concurrentperformance.ringingmaster.engine.touch.proof.Proof;
import com.concurrentperformance.ringingmaster.engine.touch.proof.ProofTerminationReason;
import com.concurrentperformance.ringingmaster.engine.touch.container.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.container.TouchCheckingType;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.SpecifiedPartsVariance;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.TouchBuilder;
import com.concurrentperformance.ringingmaster.engine.touch.container.impl.VarianceLogicType;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Stephen
 */
public class LeadBasedCompilerTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Parser parser = new DefaultParser();

	@Test
	public void canConstructAllChangeLead() {
		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8, NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE));

		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
		touch.setTerminationMaxLeads(1);
		Proof result = new LeadBasedCompiler(touch).compile(false);
		Method method = result.getCreatedMethod().get();

		assertNotNull("Should return non null Method", method);
		assertEquals("X should produce an initial rounds row, and a single changed row", 2, method.getLead(0).getRowCount());
		assertEquals("Row 0 should be rounds", "12345678", method.getLead(0).getRow(0).getDisplayString(false));
		assertEquals("Row 1 should be all change", "21436587", method.getLead(0).getRow(1).getDisplayString(false));
	}

	@Test
	public void canConstructChangePlaceLead() {
		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8, NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4));

		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
		touch.setTerminationMaxLeads(1);
		Proof result = new LeadBasedCompiler(touch).compile(false);
		Method method = result.getCreatedMethod().get();

		assertEquals("14 should produce an initial rounds row, and a single changed row", 2, method.getLead(0).getRowCount());
		assertEquals("Row 0 should be rounds", "12345678", method.getLead(0).getRow(0).getDisplayString(false));
		assertEquals("Row 1 should have place 1 & 4 made", "13246587", method.getLead(0).getRow(1).getDisplayString(false));
	}

	@Ignore
	@Test
	public void leadSeparatorPositionsCorrect() {
		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8,
				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4));

		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
		touch.setTerminationMaxLeads(1);
		Proof result = new LeadBasedCompiler(touch).compile(false);
		Method method = result.getCreatedMethod().get();

		assertArrayEquals(new int[]{1}, method.getLead(0).getLeadSeparatorPositions());
	}

	@Test
	public void leadCountTerminationCorrectlyCountsLeads() {
		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8,
				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4),
				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE));

		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
		touch.removeTerminationSpecificRow();

		for (int i=1; i< 50; i++) {

			touch.setTerminationMaxLeads(i);
			Proof result = new LeadBasedCompiler(touch).compile(false);
			Method method = result.getCreatedMethod().get();

			assertEquals(i, method.getLeadCount());
			assertEquals(ProofTerminationReason.LEAD_COUNT, result.getTerminationReason());
		}
	}

	@Test
	public void rowCountTerminationCorrectlyCountsRows() {
		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_8,
				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4),
				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_4));

		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
		touch.removeTerminationSpecificRow();

		for (int i=1; i< 200; i++) {
			touch.setTerminationMaxRows(i);
			Proof result = new LeadBasedCompiler(touch).compile(false);
			Method method = result.getCreatedMethod().get();

			assertEquals(i, method.getRowCount());
			assertEquals(ProofTerminationReason.ROW_COUNT, result.getTerminationReason());
		}
	}

	@Test
	public void rowTerminationCorrectlyStopsAtRounds() {

		final NotationBody mockedNotationBody = mockNotation(NumberOfBells.BELLS_6,
				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_6),
				NotationRowHelper.buildNotationRow(NotationPlace.ALL_CHANGE),
				NotationRowHelper.buildNotationRow(NotationPlace.PLACE_1, NotationPlace.PLACE_6));

		final MethodRow roundsRow = MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6);
		Touch touch = TouchBuilder.buildPlainCourseInstance(mockedNotationBody);
		touch.setTerminationSpecificRow(roundsRow);
		Proof result = new LeadBasedCompiler(touch).compile(false);
		Method method = result.getCreatedMethod().get();

		assertEquals(roundsRow, method.getLastRow());
	}

	@Test
	public void compilePlainCourseOfPlainBobMinor() throws IOException {
		Touch touch = TouchBuilder.buildPlainCourseInstance(buildPlainBobMinor());
		Proof result = new LeadBasedCompiler(touch).compile(false);
		assertEquals("Plain Course of Plain Bob Minor", result.getTouch().getTitle());
		assertEquals(60, result.getCreatedMethod().get().getRowCount());
		assertEquals(5, result.getCreatedMethod().get().getLeadCount());
		checkAgainstFile(result.getCreatedMethod().get(), "PlainBobMinor.txt");
	}

	@Test(expected = IllegalArgumentException.class)
	public void compilingTouchWithNoNotationThrows() {
		Touch touch = null;
		try {
			touch = TouchBuilder.getInstance(NumberOfBells.BELLS_6, 1, 1);
			touch.setTerminationMaxRows(10);
			touch.setTouchCheckingType(TouchCheckingType.LEAD_BASED);
		} catch (Exception e) {
			fail();
		}
		new LeadBasedCompiler(touch);
	}

	@Test
	public void compileSingleCall() throws IOException {
		checkSimple1CellPlainBobTouch("-", 3, "PlainBobMinor - .txt", true);
	}

	@Test
	public void compileCallMultiplier() throws IOException {
		checkSimple1CellPlainBobTouch("2-", 3, "PlainBobMinor - .txt", true);
	}

	@Test
	public void compileCallAndPlain() throws IOException {
		checkSimple1CellPlainBobTouch("-p", 10, "PlainBobMinor -p .txt", true);
	}

	@Test
	public void compileCallAndPlainMultiplier() throws IOException {
		checkSimple1CellPlainBobTouch("-2p", 6, "PlainBobMinor -2p .txt", true);
	}

	@Test
	public void compileGroup() throws IOException {
		checkSimple1CellPlainBobTouch("2(-p)s", 30, "PlainBobMinor 2(-p)s .txt", false);
	}

	@Test
	public void compileEmbeddedGroup() throws IOException {
		checkSimple1CellPlainBobTouch("2(2(-p)s)-", 22, "PlainBobMinor 2(2(-p)s) .txt", false);
	}

	@Test
	public void compileOmitParts() throws IOException {
		Touch touch = buildPlainBobMinorTouchShell();
		SpecifiedPartsVariance omitFromPart2 = new SpecifiedPartsVariance(VarianceLogicType.EXCLUDE, Sets.newHashSet(1));
		touch.insertCharacter(0, 0, 0, '-');
		touch.insertCharacter(0, 0, 1, '[').setVariance(omitFromPart2);
		touch.insertCharacter(0, 0, 2, 's');
		touch.insertCharacter(0, 0, 3, ']');
		parser.parseAndAnnotate(touch);
		proveAndCheckTouch(6, "PlainBobMinor -[s] omit2.txt", true, ProofTerminationReason.SPECIFIED_ROW, touch);
	}

	@Test
	public void compileEmptyPartsTerminatedWithEmptyParts() throws IOException {
		Touch touch = buildPlainBobMinorTouchShell();
		SpecifiedPartsVariance omitFromPart1_2_3 = new SpecifiedPartsVariance(VarianceLogicType.EXCLUDE, Sets.newHashSet(0,1));
		touch.insertCharacter(0, 0, 0, '[').setVariance(omitFromPart1_2_3);
		touch.insertCharacter(0, 0, 1, '-');
		touch.insertCharacter(0, 0, 2, 's');
		touch.insertCharacter(0, 0, 3, ']');
		parser.parseAndAnnotate(touch);
		proveAndCheckTouch(0, "PlainBobMinor [-s] omit1_2_3.txt", true, ProofTerminationReason.EMPTY_PARTS, touch);
	}

	@Test
	public void compileDefinitionWithGroup() throws IOException {
		Touch touch = buildPlainBobMinorTouchShell();
		touch.addCharacters(0, 0, "-def-");
		touch.addDefinition("def", "2(s-)");
		parser.parseAndAnnotate(touch);
		proveAndCheckTouch(6, "PlainBobMinor -def-.txt", false, ProofTerminationReason.SPECIFIED_ROW, touch);
	}

	Proof checkSimple1CellPlainBobTouch(String touchString, int expectedLeadCount, String fileName, boolean trueTouch) throws IOException {
		Touch touch = buildPlainBobMinorTouchShell();
		touch.addCharacters(0, 0, touchString);
		parser.parseAndAnnotate(touch);
		return proveAndCheckTouch(expectedLeadCount, fileName, trueTouch, ProofTerminationReason.SPECIFIED_ROW, touch);
	}

	private Touch buildPlainBobMinorTouchShell() {
		Touch touch = TouchBuilder.getInstance(NumberOfBells.BELLS_6, 1, 1);
		touch.addNotation(buildPlainBobMinor());
		touch.setTouchCheckingType(TouchCheckingType.LEAD_BASED);
		touch.setTerminationSpecificRow(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6));
		touch.setPlainLeadToken("p");
		return touch;
	}

	private Proof proveAndCheckTouch(int expectedLeadCount, String fileName, boolean trueTouch,
	                                 ProofTerminationReason terminationReason, Touch touch) throws IOException {
		Proof proof = new LeadBasedCompiler(touch).compile(true);
		assertEquals(terminationReason, proof.getTerminationReason());
		assertEquals(expectedLeadCount, proof.getCreatedMethod().get().getLeadCount());
		checkAgainstFile(proof.getCreatedMethod().get(), fileName);
		assertEquals(trueTouch, proof.getAnalysis().get().isTrueTouch());
		return proof;
	}

	private void checkAgainstFile(Method method, String fileName) throws IOException {
		String allChangesAsText = method.getAllChangesAsText();
		String fileContent;
		try (InputStream stream = getClass().getResourceAsStream(fileName)) {
			fileContent = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
		}

		Assert.assertEquals(convertToLineSeparators(fileContent), convertToLineSeparators(allChangesAsText));
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

	private NotationBody mockNotation(final NumberOfBells numberOfBells, final NotationRow... rows) {
		final NotationBody mockedNotationBody = mock(NotationBody.class);

		when(mockedNotationBody.iterator()).thenAnswer(new Answer<Iterator<NotationRow>>() {
			@Override
			public Iterator<NotationRow> answer(final InvocationOnMock invocation) {
				return Arrays.asList(rows).iterator();
			}
		});
		when(mockedNotationBody.getNumberOfWorkingBells()).thenReturn(numberOfBells);
		when(mockedNotationBody.getRowCount()).thenReturn(rows.length);
		for (int i=0;i<rows.length;i++) {
			when(mockedNotationBody.getRow(i)).thenReturn(rows[i]);

		}
		when(mockedNotationBody.getName()).thenReturn("Unnamed");
		when(mockedNotationBody.getNameIncludingNumberOfBells()).thenReturn("Unnamed " + numberOfBells.getName());

		return mockedNotationBody;
	}
	//TODO compound terminations


}
