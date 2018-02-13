package org.ringingmaster.engine.compiler.impl;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.proof.Proof;
import org.ringingmaster.engine.proof.ProofTerminationReason;
import org.ringingmaster.engine.touch.ObservableTouch;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.checkingtype.CheckingType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ringingmaster.engine.touch.TableType.TOUCH_TABLE;


/**
 * TODO comments???
 * User: Stephen
 */
public class CourseBasedCompilerTest {

	private final Parser parser = new Parser();

	@Test
	public void compileSingleCallCalledFromTenor() throws IOException {
		ObservableTouch touch = buildPlainBobMinorTouchShell();
		touch.addCharacters(TOUCH_TABLE,0, 0, "W");
		touch.addCharacters(TOUCH_TABLE,1, 0, "-");
		Proof proof = parseProveAndCheckTouch(15
				, "/PlainBobMinor W - FromTenor.txt", true, ProofTerminationReason.SPECIFIED_ROW, touch.get());
	}

	@Test
	public void compileSingleCallCalledFrom5() throws IOException {
		fail(); //TODO

//		Touch touch = buildPlainBobMinorTouchShell(1, 2);
//		touch.setCallFromBell(Bell.BELL_5);
//		touch.addCharacters(0, 0, "W");
//		touch.addCharacters(0, 1, "-");
//		Proof proof = parseProveAndCheckTouch(15
//				, "/PlainBobMinor W - From5.txt", true, ProofTerminationReason.SPECIFIED_ROW, touch);
	}

	private ObservableTouch buildPlainBobMinorTouchShell() {
		ObservableTouch touch = new ObservableTouch();
		touch.setNumberOfBells(NumberOfBells.BELLS_6);
		touch.setTitle("Test Touch");
		touch.addNotation(buildPlainBobMinor());
		touch.setTouchCheckingType(CheckingType.COURSE_BASED);
		return touch;
	}

	private NotationBody buildPlainBobMinor() {
		return NotationBuilder.getInstance()
				.setNumberOfWorkingBells(NumberOfBells.BELLS_6)
				.setName("Plain Bob")
				.setFoldedPalindromeNotationShorthand("-16-16-16", "12")
				.addCall("Bob", "-", "14", true)
				.addCall("Single", "s", "1234", false)
				.addCallInitiationRow(11)
				.addMethodCallingPosition("W", 11, 0)
				.build();
	}

	private Proof parseProveAndCheckTouch(int expectedLeadCount, String fileName, boolean trueTouch,
	                                      ProofTerminationReason terminationReason, Touch touch) throws IOException {
        final Parse parse = parser.apply(touch);

        Proof proof = new CourseBasedCompiler(touch, "").compile(true, () -> false);
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

		assertEquals(convertToOsLineSeparators(fileContent), convertToOsLineSeparators(allChangesAsText));
	}

	private String convertToOsLineSeparators(String text) {
		text = text.replace("\r\n", System.lineSeparator());
		text = text.replace("\r", System.lineSeparator());
		text = text.replace("\n", System.lineSeparator());
		return text;
	}

}
