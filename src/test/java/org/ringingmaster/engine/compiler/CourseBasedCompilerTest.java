package org.ringingmaster.engine.compiler;

import org.junit.Test;
import org.ringingmaster.engine.parser.Parser;

import java.io.IOException;

import static org.junit.Assert.fail;


/**
 * TODO comments???
 * User: Stephen
 */
public class CourseBasedCompilerTest {

	private final Parser parser = new Parser();

//	@Test
//	public void compileSingleCallCalledFromTenor() throws IOException {
//		ObservableComposition composition = buildPlainBobMinorCompositionShell();
//		composition.addCharacters(MAIN_TABLE,0, 0, "W");
//		composition.addCharacters(MAIN_TABLE,1, 0, "-");
//		parseProveAndCheckComposition(15
//				, "/PlainBobMinor W - FromTenor.txt", true, CompileTerminationReason.SPECIFIED_ROW, composition.get());
//	}

	@Test
	public void compileSingleCallCalledFrom5() throws IOException {
		fail(); //TODO

//		Composition composition = buildPlainBobMinorCompositionShell(1, 2);
//		composition.setCallFromBell(Bell.BELL_5);
//		composition.addCharacters(0, 0, "W");
//		composition.addCharacters(0, 1, "-");
//		CompiledComposition compiledComposition = parseProveAndCheckComposition(15
//				, "/PlainBobMinor W - From5.txt", true, CompileTerminationReason.SPECIFIED_ROW, composition);
	}
//
//	private ObservableComposition buildPlainBobMinorCompositionShell() {
//		ObservableComposition composition = new ObservableComposition();
//		composition.setNumberOfBells(NumberOfBells.BELLS_6);
//		composition.setTitle("Test Composition");
//		composition.addNotation(buildPlainBobMinor());
//		composition.setCheckingType(CheckingType.COURSE_BASED);
//		return composition;
//	}
//
//	private NotationBody buildPlainBobMinor() {
//		return NotationBuilder.getInstance()
//				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
//				.setName("Plain Bob")
//				.setFoldedPalindromeNotationShorthand("-16-16-16", "12")
//				.addCall("Bob", "-", "14", true)
//				.addCall("Single", "s", "1234", false)
//				.addCallInitiationRow(11)
//				.addMethodCallingPosition("W", 11, 0)
//				.build();
//	}
//
//	private CompiledComposition parseProveAndCheckComposition(int expectedLeadCount, String fileName, boolean trueComposition,
//										  CompileTerminationReason terminationReason, Composition composition) throws IOException {
//        final Parse parse = parser.apply(composition);
//
//        CompiledComposition compiledComposition = new CourseBasedCompiler(composition, "").compile(true, () -> false);
//		assertEquals(terminationReason, compiledcomposition.getTerminationReason());
//		assertEquals(expectedLeadCount, compiledcomposition.getMethod().get().getLeadCount());
//		checkAgainstFile(compiledcomposition.getMethod().get(), fileName);
//		assertEquals(trueComposition, compiledcomposition.getAnalysis().get().isTrueComposition());
//		return compiledcomposition;
//	}
//
//	private void checkAgainstFile(Method method, String fileName) throws IOException {
//		String allChangesAsText = method.getAllChangesAsText();
//		String fileContent;
//		try (InputStream stream = getClass().getResourceAsStream(fileName)) {
//			fileContent = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
//		}
//
//		assertEquals(convertToOsLineSeparators(fileContent), convertToOsLineSeparators(allChangesAsText));
//	}
//
//	private String convertToOsLineSeparators(String text) {
//		text = text.replace("\r\n", System.lineSeparator());
//		text = text.replace("\r", System.lineSeparator());
//		text = text.replace("\n", System.lineSeparator());
//		return text;
//	}

}
