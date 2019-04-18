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
//		ObservableTouch touch = buildPlainBobMinorTouchShell();
//		touch.addCharacters(TOUCH_TABLE,0, 0, "W");
//		touch.addCharacters(TOUCH_TABLE,1, 0, "-");
//		parseProveAndCheckTouch(15
//				, "/PlainBobMinor W - FromTenor.txt", true, CompileTerminationReason.SPECIFIED_ROW, touch.get());
//	}

	@Test
	public void compileSingleCallCalledFrom5() throws IOException {
		fail(); //TODO

//		Touch touch = buildPlainBobMinorTouchShell(1, 2);
//		touch.setCallFromBell(Bell.BELL_5);
//		touch.addCharacters(0, 0, "W");
//		touch.addCharacters(0, 1, "-");
//		CompiledTouch compiledtouch = parseProveAndCheckTouch(15
//				, "/PlainBobMinor W - From5.txt", true, CompileTerminationReason.SPECIFIED_ROW, touch);
	}
//
//	private ObservableTouch buildPlainBobMinorTouchShell() {
//		ObservableTouch touch = new ObservableTouch();
//		touch.setNumberOfBells(NumberOfBells.BELLS_6);
//		touch.setTitle("Test Touch");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setCheckingType(CheckingType.COURSE_BASED);
//		return touch;
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
//	private CompiledTouch parseProveAndCheckTouch(int expectedLeadCount, String fileName, boolean trueTouch,
//										  CompileTerminationReason terminationReason, Touch touch) throws IOException {
//        final Parse parse = parser.apply(touch);
//
//        CompiledTouch compiledtouch = new CourseBasedCompiler(touch, "").compile(true, () -> false);
//		assertEquals(terminationReason, compiledtouch.getTerminationReason());
//		assertEquals(expectedLeadCount, compiledtouch.getMethod().get().getLeadCount());
//		checkAgainstFile(compiledtouch.getMethod().get(), fileName);
//		assertEquals(trueTouch, compiledtouch.getAnalysis().get().isTrueTouch());
//		return compiledtouch;
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
