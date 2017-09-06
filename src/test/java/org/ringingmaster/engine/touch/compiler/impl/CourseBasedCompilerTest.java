package org.ringingmaster.engine.touch.compiler.impl;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Assert;
import org.junit.Test;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.parser.Parser;
import org.ringingmaster.engine.parser.impl.DefaultParser;
import org.ringingmaster.engine.touch.newcontainer.Touch;
import org.ringingmaster.engine.touch.proof.Proof;
import org.ringingmaster.engine.touch.proof.ProofTerminationReason;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * TODO comments???
 * User: Stephen
 */
public class CourseBasedCompilerTest {

	private final Parser parser = new DefaultParser();

	@Test
	public void compileSingleCallCalledFromTenor() throws IOException {
		fail(); //TODO

//		Touch touch = buildPlainBobMinorTouchShell(1, 2);
//		touch.addCharacters(0, 0, "W");
//		touch.addCharacters(0, 1, "-");
//		Proof proof = parseProveAndCheckTouch(15
//				, "/PlainBobMinor W - FromTenor.txt", true, ProofTerminationReason.SPECIFIED_ROW, touch);
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

	private Touch buildPlainBobMinorTouchShell(int width, int height) {
		fail(); //TODO

		return null;
//		Touch touch = TouchBuilder.newTouch(NumberOfBells.BELLS_6, width, height);
//		touch.setTitle("Test Touch");
//		touch.addNotation(buildPlainBobMinor());
//		touch.setTouchCheckingType(CheckingType.COURSE_BASED);
//		touch.setTerminationChange(MethodBuilder.buildRoundsRow(NumberOfBells.BELLS_6));
//		return touch;
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
		parser.parseAndAnnotate(touch);
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

		Assert.assertEquals(convertToOsLineSeparators(fileContent), convertToOsLineSeparators(allChangesAsText));
	}

	private String convertToOsLineSeparators(String text) {
		text = text.replace("\r\n", System.lineSeparator());
		text = text.replace("\r", System.lineSeparator());
		text = text.replace("\n", System.lineSeparator());
		return text;
	}

}
